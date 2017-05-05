package com.englishtown.vertx.mail;

/**
 * Mail configuration
 */
public interface MailConfigurator {

    String getTransportProtocol();

    String getHost();

    int getPort();

    int getConnectTimeout();

    int getTimeout();

    String getUsername();

    String getPassword();

    boolean getStartTlsEnable();

    boolean getStartTlsRequired();

    boolean isDebug();

}
