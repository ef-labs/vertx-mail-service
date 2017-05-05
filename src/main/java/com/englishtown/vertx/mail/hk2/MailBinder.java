package com.englishtown.vertx.mail.hk2;

import com.englishtown.vertx.mail.MailConfigurator;
import com.englishtown.vertx.mail.MailService;
import com.englishtown.vertx.mail.SessionFactory;
import com.englishtown.vertx.mail.impl.SmtpMailService;
import com.englishtown.vertx.mail.impl.DefaultSessionFactory;
import com.englishtown.vertx.mail.impl.DefaultMailConfigurator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * HK2 binder for mail service
 */
public class MailBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(SmtpMailService.class).to(MailService.class).in(Singleton.class);
        bind(DefaultSessionFactory.class).to(SessionFactory.class).in(Singleton.class);
        bind(DefaultMailConfigurator.class).to(MailConfigurator.class).in(Singleton.class);
    }

}
