package de.codeptibull.vertx.reactivestreams;

import de.codepitbull.akka.reactivestreams.RandomValuesFlow;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.reactivestreams.ReactiveReadStream;
import io.vertx.rxjava.core.AbstractVerticle;

/**
 * Created by jmader on 19.04.15.
 */
public class VertxReactiveStreamsVerticle extends AbstractVerticle{

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        ReactiveReadStream<Buffer> reactiveReadStream = ReactiveReadStream.readStream(10);

        RandomValuesFlow randomValuesFlow = new RandomValuesFlow();
        randomValuesFlow.start(reactiveReadStream);

        vertx.eventBus().<Boolean>consumer("flowctrl", req -> {
            if (req.body()) {
                reactiveReadStream.handler(event -> vertx.eventBus().send("target", event));
            } else
                reactiveReadStream.pause();
        }).completionHandler(res -> startFuture.complete());
    }
}
