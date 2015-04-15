package com.englishtown.vertx.mail.impl;

import com.englishtown.vertx.mail.TransportDelegate;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

/**
 * Default implementation of {@link com.englishtown.vertx.mail.TransportDelegate}
 */
public class DefaultTransportDelegate implements TransportDelegate {
    @Override
    public void send(Message msg) throws MessagingException {
        Transport.send(msg);
    }
}
