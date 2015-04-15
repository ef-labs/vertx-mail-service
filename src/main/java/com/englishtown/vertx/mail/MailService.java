package com.englishtown.vertx.mail;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.ProxyIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ProxyHelper;

/**
 * Interface of Mail service
 */
@VertxGen
@ProxyGen
public interface MailService {

    /**
     * Create proxy for MailService event bus
     *
     * @param vertx
     * @param address of event bus
     * @return
     */
    static MailService createEventBusProxy(Vertx vertx, String address) {
        return ProxyHelper.createProxy(MailService.class, vertx, address);
    }

    @ProxyIgnore
    void start();

    @ProxyIgnore
    void stop();

    void send(SendOptions options, Handler<AsyncResult<Void>> resultHandler);

}
