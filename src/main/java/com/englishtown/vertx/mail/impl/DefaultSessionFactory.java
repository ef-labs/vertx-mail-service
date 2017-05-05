package com.englishtown.vertx.mail.impl;

import com.englishtown.vertx.mail.SessionFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.Properties;

/**
 * Default implementation of {@link SessionFactory}
 */
public class DefaultSessionFactory implements SessionFactory {

    @Override
    public Session getInstance(Properties props) {
        return Session.getInstance(props);
    }
}
