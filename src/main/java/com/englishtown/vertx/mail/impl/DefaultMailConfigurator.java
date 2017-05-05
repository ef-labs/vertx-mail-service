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
    public static final String DEFAULT_TRANSPORT_PROTOCOL = "smtp";

    public static final String CONFIG_FIELD_TRANSPORT_PROTOCOL = "transport_protocol";
    public static final String CONFIG_FIELD_HOST = "host";
    public static final String CONFIG_FIELD_PORT = "port";
    public static final String CONFIG_FIELD_CONNECT_TIMEOUT = "connect_timeout";
    public static final String CONFIG_FIELD_TIMEOUT = "timeout";
    public static final String CONFIG_FIELD_USERNAME = "username";
    public static final String CONFIG_FIELD_PASSWORD = "password";
    public static final String CONFIG_FIELD_STARTTLS_ENABLE = "starttls_enable";
    public static final String CONFIG_FIELD_STARTTLS_REQUIRED = "starttls_required";
    public static final String CONFIG_FIELD_DEBUG = "debug";

    public static final String ENV_MAIL_TRANSPORT_PROTOCOL = "MAIL_TRANSPORT_PROTOCOL";
    public static final String ENV_MAIL_HOST = "MAIL_HOST";
    public static final String ENV_MAIL_PORT = "MAIL_PORT";
    public static final String ENV_MAIL_CONNECT_TIMEOUT = "MAIL_CONNECT_TIMEOUT";
    public static final String ENV_MAIL_TIMEOUT = "MAIL_TIMEOUT";
    public static final String ENV_MAIL_USERNAME = "MAIL_USERNAME";
    public static final String ENV_MAIL_PASSWORD = "MAIL_PASSWORD";
    public static final String ENV_MAIL_STARTTLS_ENABLE = "MAIL_STARTTLS_ENABLE";
    public static final String ENV_MAIL_STARTTLS_REQUIRED = "MAIL_STARTTLS_REQUIRED";
    public static final String ENV_MAIL_DEBUG = "MAIL_DEBUG";

    private final JsonObject config;

    private String protocol;
    private String host;
    private int port;
    private int connectTimeout;
    private int timeout;
    private String username;
    private String password;
    private boolean startTlsEnable;
    private boolean startTlsRequired;
    private boolean debug;

    @Inject
    public DefaultMailConfigurator(Vertx vertx) {
        JsonObject config = vertx.getOrCreateContext().config();
        this.config = config.getJsonObject("mail", config);
        init();
    }

    private void init() {
        protocol = getStringValue(CONFIG_FIELD_TRANSPORT_PROTOCOL, ENV_MAIL_TRANSPORT_PROTOCOL, DEFAULT_TRANSPORT_PROTOCOL);
        host = getStringValue(CONFIG_FIELD_HOST, ENV_MAIL_HOST, DEFAULT_HOST);
        port = getIntValue(CONFIG_FIELD_PORT, ENV_MAIL_PORT, DEFAULT_PORT);
        connectTimeout = getIntValue(CONFIG_FIELD_CONNECT_TIMEOUT, ENV_MAIL_CONNECT_TIMEOUT, DEFAULT_SMTP_CONNECT_TIMEOUT);
        timeout = getIntValue(CONFIG_FIELD_TIMEOUT, ENV_MAIL_TIMEOUT, DEFAULT_SMTP_TIMEOUT);
        username = getStringValue(CONFIG_FIELD_USERNAME, ENV_MAIL_USERNAME, null);
        password = getStringValue(CONFIG_FIELD_PASSWORD, ENV_MAIL_PASSWORD, null);
        startTlsEnable = getBooleanValue(CONFIG_FIELD_STARTTLS_ENABLE, ENV_MAIL_STARTTLS_ENABLE, false);
        startTlsRequired = getBooleanValue(CONFIG_FIELD_STARTTLS_REQUIRED, ENV_MAIL_STARTTLS_REQUIRED, false);
        debug = getBooleanValue(CONFIG_FIELD_DEBUG, ENV_MAIL_DEBUG, false);
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

    private boolean getBooleanValue(String configKey, String envKey, boolean defVal) {
        Boolean val = config.getBoolean(configKey);

        if (val != null) {
            return val.booleanValue();
        }

        String s = System.getenv(envKey);

        if (s != null) {
            return Boolean.valueOf(s).booleanValue();
        }

        return defVal;
    }

    @Override
    public String getTransportProtocol() {
        return protocol;
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

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean getStartTlsEnable() {
        return startTlsEnable;
    }

    @Override
    public boolean getStartTlsRequired() {
        return startTlsRequired;
    }

    @Override
    public boolean isDebug() {
        return debug;
    }
}
