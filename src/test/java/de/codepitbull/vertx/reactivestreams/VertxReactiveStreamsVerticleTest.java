package de.codepitbull.vertx.reactivestreams;

import de.codeptibull.vertx.reactivestreams.ReactiveStreamsVerticle;
import de.codeptibull.vertx.reactivestreams.VertxReactiveStreamsVerticle;
import io.vertx.test.core.VertxTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jmader on 03.04.15.
 */
public class VertxReactiveStreamsVerticleTest extends VertxTestBase {

    @Before
    public void setUpTest() throws Exception{
        vertx.deployVerticle(VertxReactiveStreamsVerticle.class.getName());
        waitUntil(() -> vertx.deploymentIDs().size() == 1);
    }

    @Test
    public void test() {
        AtomicInteger counter = new AtomicInteger();
        vertx.eventBus().consumer("target", consume ->{
            int newVal = counter.incrementAndGet();
            if(newVal == 10)
                testComplete();
            if(newVal > 10)
                fail();
        }).completionHandler(res -> {
            vertx.eventBus().send("flowctrl", Boolean.TRUE);
        });
        await();
    }
}
