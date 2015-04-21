package de.codepitbull.vertx.reactivestreams;

import de.codeptibull.vertx.reactivestreams.VertxReactiveStreamsVerticle;
import de.codeptibull.vertx.reactivestreams.VertxReactiveStreamsWriteToFileVerticle;
import io.vertx.test.core.VertxTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jmader on 03.04.15.
 */
public class VertxReactiveStreamsWriteToFileVerticleTest extends VertxTestBase {

    @Before
    public void setUpTest() throws Exception{
        vertx.fileSystem().delete("/tmp/myfile", res -> {
            vertx.deployVerticle(VertxReactiveStreamsWriteToFileVerticle.class.getName());
        });
        waitUntil(() -> vertx.deploymentIDs().size() == 1);
    }

    @Test
    public void test() {
        vertx.eventBus().send("flowctrl", Boolean.TRUE);
        vertx.setTimer(500, fired -> {
            vertx.eventBus().send("flowctrl", Boolean.FALSE, response -> {
                assertTrue(vertx.fileSystem().existsBlocking("/tmp/myfile"));
                testComplete();
            });
        });
        await();
    }
}
