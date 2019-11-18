package io.vertx.blog.first;

import io.vertx.core.*;

public class MyFirstVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        final Future<String> firstFuture = Future.future();
        Future<String> secondFuture = Future.future();
        final Future<String> thirdFuture = Future.future();

        DeploymentOptions options = new DeploymentOptions().setWorker(true);

            vertx.deployVerticle(FirstWorkerVerticle.class.getName(), options, result -> {
                if (result.succeeded()) {
                    System.out.println("Successful");
                    firstFuture.complete();
                } else {
                    firstFuture.fail(result.cause());
                }

            });


        firstFuture.compose(futureResult1 ->{
            vertx.deployVerticle(SecondWorkerVerticle.class.getName(), options, result -> {

                if (result.succeeded()) {
                    System.out.println("Successful 2");
                    secondFuture.complete();
                } else {
                    secondFuture.fail(result.cause());
                }

            });
            return secondFuture;
        }).compose(futureResult2 -> {
            System.out.println(" main verticle");

            vertx.createHttpServer()
                    .requestHandler(r -> {
                        r.response().end("<h1>Hello from my first " +
                                "Vert.x 3 application</h1>");
                    })
                    .listen(8080,result -> {
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
                fut.complete();
            }else {
                System.out.println("Main Verticle deploy failed");

                fut.fail(asyncResult.cause());
            }
        });

    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MyFirstVerticle.class.getName());
    }


}
