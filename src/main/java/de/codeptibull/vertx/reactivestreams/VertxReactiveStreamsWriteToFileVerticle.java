package de.codeptibull.vertx.reactivestreams;

import de.codepitbull.akka.reactivestreams.RandomValuesFlow;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.streams.Pump;
import io.vertx.ext.reactivestreams.ReactiveReadStream;
import io.vertx.core.AbstractVerticle;

/**
 * Created by jmader on 19.04.15.
 */
public class VertxReactiveStreamsWriteToFileVerticle extends AbstractVerticle{

    private Pump pump;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        ReactiveReadStream<Buffer> reactiveReadStream = ReactiveReadStream.readStream(10);

        RandomValuesFlow randomValuesFlow = new RandomValuesFlow();
        randomValuesFlow.start(reactiveReadStream);
        vertx.fileSystem().open("/tmp/myfile", new OpenOptions().setCreate(true).setWrite(true), res1 -> {
            pump = Pump.pump(reactiveReadStream, res1.result());
            vertx.eventBus().<Boolean>consumer("flowctrl", req -> {
                if (req.body()) {
                    pump.start();
                    req.reply(true);
                }
                else {
                    pump.stop();
                    req.reply(false);
                }
            }).completionHandler(res2 -> startFuture.complete());
        });


    }
}
