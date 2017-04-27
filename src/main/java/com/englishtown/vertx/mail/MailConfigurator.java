package com.englishtown.vertx.mail;

/**
 * Mail configuration
 */
public interface MailConfigurator {

    String getHost();

    int getPort();

    int getConnectTimeout();

    int getTimeout();

}
