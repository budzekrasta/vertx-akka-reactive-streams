package de.codeptibull.vertx.reactivestreams;

import de.codepitbull.akka.reactivestreams.RandomValuesFlow;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.streams.Pump;
import io.vertx.ext.reactivestreams.ReactiveReadStream;

/**
 * Created by jmader on 19.04.15.
 */
public class VertxReactiveStreamsWriteToHttp extends AbstractVerticle{

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.createHttpServer()
                .requestHandler(req -> {
                    req.response().setChunked(true);
                    ReactiveReadStream<Buffer> reactiveReadStream = ReactiveReadStream.readStream(10);
                    new RandomValuesFlow().start(reactiveReadStream);
                    Pump pump = Pump.pump(reactiveReadStream, req.response());
                    vertx.setTimer(500, timer -> {
                        pump.stop();
                        req.response().end();
                    });
                    pump.start();
                })
                .listen(8090, res -> {
                    if (res.failed())
                        startFuture.fail(res.cause());
                    else startFuture.complete();
                });

    }
}
