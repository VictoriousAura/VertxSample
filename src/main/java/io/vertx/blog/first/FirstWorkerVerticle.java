package io.vertx.blog.first;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;

public class FirstWorkerVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        EventBus eb = vertx.eventBus();
        MessageConsumer<String> consumer = eb.consumer("FirstWorkerVerticle");
        consumer.handler(message -> System.out.println("I have received a message from 1st worker verticle: " + message.body()));
        consumer.completionHandler(res -> {
            if (res.succeeded()) {
                fut.complete();
                System.out.println("The handler registration has reached all nodes");
            } else {
                fut.fail(res.cause());
                System.out.println("Registration failed!");
            }
        });
        eb.send("FirstWorkerVerticle","Msg from 1st worker verticle");

    }
}
