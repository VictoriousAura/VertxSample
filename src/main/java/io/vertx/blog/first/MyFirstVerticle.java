package io.vertx.blog.first;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class MyFirstVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        Future<String> firstFurture = Future.future();
        Future<String> secondFuture = Future.future();
        Future<String> thirdFuture = Future.future();

        DeploymentOptions options = new DeploymentOptions().setWorker(true);

            vertx.deployVerticle(FirstWorkerVerticle.class.getName(), options, result -> {
                if (result.succeeded()) {
                    System.out.println("Successful");
                    firstFurture.complete();
                } else {
                    firstFurture.fail(result.cause());
                }

            });


        firstFurture.compose(futureResult1 -> {
            vertx.deployVerticle(SecondWorkerVerticle.class.getName(), options, result -> {
                if (result.succeeded()) {
                    System.out.println("Successful");
                    secondFuture.complete();
                } else {
                    secondFuture.fail(result.cause());
                }

            });
            return secondFuture;
        });

        secondFuture.compose(futureResult2 -> {

            vertx.createHttpServer()
                    .requestHandler(r -> {
                        r.response().end("<h1>Hello from my first " +
                                "Vert.x 3 application</h1>");
                    })
                    .listen(result -> {
                        if (result.succeeded()) {
                            System.out.println("http port: " + result.result().actualPort());
                            thirdFuture.complete();
                        } else {
                            thirdFuture.fail(result.cause());
                        }
                    });
            return thirdFuture;
        }).setHandler(asyncResult -> {
            if(asyncResult.succeeded()){
                System.out.println("Main Verticle deployed");
                thirdFuture.complete();
            }else {
                thirdFuture.fail(asyncResult.cause());
            }
        });

    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MyFirstVerticle.class.getName());
    }

}
