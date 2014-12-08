package com.englishtown.vertx.mail;

import io.vertx.core.AbstractVerticle;
import io.vertx.serviceproxy.ProxyHelper;

import javax.inject.Inject;

/**
 * Proxy Mail Service Event Bus Verticle
 */
public class MailServiceVerticle extends AbstractVerticle {

    private static final String CONFIG_FIELD_ADDRESS = "address";
    public static final String DEFAULT_ADDRESS = "et.mail";

    private final MailService service;

    @Inject
    public MailServiceVerticle(MailService service) {
        this.service = service;
    }

    @Override
    public void start() throws Exception {

        String address = config().getString(CONFIG_FIELD_ADDRESS, DEFAULT_ADDRESS);

        // Register service as a EventBus handler
        ProxyHelper.registerService(MailService.class, vertx, service, address);

        // Start the service
        service.start();
    }

    @Override
    public void stop() throws Exception {
        service.stop();
    }
}
