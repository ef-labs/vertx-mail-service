package com.englishtown.vertx.mail;

import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * {@link javax.mail.Transport} delegate interface
 */
public interface TransportDelegate {

    void send(Message msg) throws MessagingException;

}
