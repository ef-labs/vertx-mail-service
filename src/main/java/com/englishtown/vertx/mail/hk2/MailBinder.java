package com.englishtown.vertx.mail.hk2;

import com.englishtown.vertx.mail.MailService;
import com.englishtown.vertx.mail.impl.DefaultMailService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * HK2 binder for mail service
 */
public class MailBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(DefaultMailService.class).to(MailService.class).in(Singleton.class);
    }

}
