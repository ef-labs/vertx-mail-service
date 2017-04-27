package com.englishtown.vertx.mail.impl;

import com.englishtown.vertx.mail.MailConfigurator;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;

/**
 * Default implementation of {@link MailConfigurator}
 */
public class DefaultMailConfigurator implements MailConfigurator {

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 25;
    public static final int DEFAULT_SMTP_CONNECT_TIMEOUT = 10000;
    public static final int DEFAULT_SMTP_TIMEOUT = 30000;

    public static final String CONFIG_FIELD_HOST = "host";
    public static final String CONFIG_FIELD_PORT = "port";
    public static final String CONFIG_FIELD_SMTP_CONNECT_TIMEOUT = "smtp_connect_timeout";
    public static final String CONFIG_FIELD_SMTP_TIMEOUT = "smtp_timeout";

    public static final String ENV_MAIL_HOST = "MAIL_HOST";
    public static final String ENV_MAIL_PORT = "MAIL_PORT";
    public static final String ENV_MAIL_CONNECT_TIMEOUT = "MAIL_CONNECT_TIMEOUT";
    public static final String ENV_MAIL_TIMEOUT = "MAIL_TIMEOUT";

    private final JsonObject config;

    private String host;
    private int port;
    private int connectTimeout;
    private int timeout;

    @Inject
    public DefaultMailConfigurator(Vertx vertx) {
        config = vertx.getOrCreateContext().config();
        init();
    }

    private void init() {
        host = getStringValue(CONFIG_FIELD_HOST, ENV_MAIL_HOST, DEFAULT_HOST);
        port = getIntValue(CONFIG_FIELD_PORT, ENV_MAIL_PORT, DEFAULT_PORT);
        connectTimeout = getIntValue(CONFIG_FIELD_SMTP_CONNECT_TIMEOUT, ENV_MAIL_CONNECT_TIMEOUT, DEFAULT_SMTP_CONNECT_TIMEOUT);
        timeout = getIntValue(CONFIG_FIELD_SMTP_TIMEOUT, ENV_MAIL_TIMEOUT, DEFAULT_SMTP_TIMEOUT);
    }

    private String getStringValue(String configKey, String envKey, String defVal) {
        String val = config.getString(configKey);

        if (val != null) {
            return val;
        }

        val = System.getenv(envKey);

        if (val != null) {
            return val;
        }

        return defVal;
    }

    private int getIntValue(String configKey, String envKey, int defVal) {
        Integer val = config.getInteger(configKey);

        if (val != null) {
            return val.intValue();
        }

        String s = System.getenv(envKey);

        if (s != null) {
            return Integer.valueOf(s).intValue();
        }

        return defVal;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }
}
