package de.codeptibull.vertx.reactivestreams;

import io.vertx.core.Vertx;

/**
 * Created by jmader on 21.04.15.
 */
public class HttpMain {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(VertxReactiveStreamsWriteToHttp.class.getName());
    }
}
