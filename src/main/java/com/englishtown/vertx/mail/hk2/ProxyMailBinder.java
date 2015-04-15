package com.englishtown.vertx.mail.hk2;

import com.englishtown.vertx.mail.MailService;
import com.englishtown.vertx.mail.MailServiceVerticle;
import io.vertx.core.Vertx;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * HK2 binder for event bus proxy injection
 */
public class ProxyMailBinder extends AbstractBinder {
    @Override
    protected void configure() {

        bindFactory(ProxyMailServiceFactory.class).to(MailService.class).in(Singleton.class).named("proxy");

    }

    static class ProxyMailServiceFactory implements Factory<MailService> {

        private final Vertx vertx;

        @Inject
        ProxyMailServiceFactory(Vertx vertx) {
            this.vertx = vertx;
        }

        @Override
        public MailService provide() {
            String address = vertx.getOrCreateContext().config().getString("mail_address", MailServiceVerticle.DEFAULT_ADDRESS);
            MailService mailService = MailService.createEventBusProxy(vertx, address);
            mailService.start();
            return mailService;
        }

        @Override
        public void dispose(MailService mailService) {
            mailService.stop();
        }
    }
}
