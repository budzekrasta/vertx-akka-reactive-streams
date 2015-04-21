package de.codeptibull.vertx.reactivestreams;

import de.codepitbull.akka.reactivestreams.RandomValuesFlow;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Context;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Created by jmader on 03.04.15.
 */
public class ReactiveStreamsVerticle extends AbstractVerticle{

    private static final Logger LOG = LoggerFactory.getLogger(ReactiveStreamsVerticle.class);

    private int issuedTokens = 0;
    private Subscription subscription;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        startAkkaAndSubscribe();

        vertx.eventBus().<Integer>consumer("request", nr -> {
            issuedTokens += nr.body();
            subscription.request(nr.body());
        }).completionHandler(res -> startFuture.complete());
    }

    private void startAkkaAndSubscribe() {
        Context ctx = vertx.getOrCreateContext();
        RandomValuesFlow randomValuesFlow = new RandomValuesFlow();
        randomValuesFlow.start(new Subscriber<Buffer>() {

            @Override
            public void onSubscribe(Subscription s) {
                ctx.runOnContext(action -> {
                    LOG.debug("SUBSCRIBED");
                    subscription = s;
                });
            }

            @Override
            public void onNext(Buffer buffer) {
                ctx.runOnContext(action -> {
                    LOG.debug("RECEIVED " + buffer);
                    vertx.eventBus().publish("target", buffer);
                    if(issuedTokens == 0) {
                        LOG.fatal("Received event without having a token");
                    }
                    issuedTokens--;
                });
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                LOG.debug("COMPLETE!!!");
            }
        });
    }
}
