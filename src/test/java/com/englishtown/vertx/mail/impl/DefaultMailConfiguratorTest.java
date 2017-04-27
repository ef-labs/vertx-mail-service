package com.englishtown.vertx.mail.impl;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DefaultMailConfigurator}
 */
public class DefaultMailConfiguratorTest {

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();
    @Rule
    public EnvironmentVariables envVars = new EnvironmentVariables();

    @Mock
    private Vertx vertx;
    @Mock
    private Context context;

    private JsonObject config = new JsonObject();

    @Before
    public void setUp() throws Exception {
        when(vertx.getOrCreateContext()).thenReturn(context);
        when(context.config()).thenReturn(config);
    }

    private DefaultMailConfigurator createInstance() {
        return new DefaultMailConfigurator(vertx);
    }

    @Test
    public void getDefaults() throws Exception {
        DefaultMailConfigurator configurator = createInstance();

        assertEquals(DefaultMailConfigurator.DEFAULT_HOST, configurator.getHost());
        assertEquals(DefaultMailConfigurator.DEFAULT_PORT, configurator.getPort());
        assertEquals(DefaultMailConfigurator.DEFAULT_SMTP_CONNECT_TIMEOUT, configurator.getConnectTimeout());
        assertEquals(DefaultMailConfigurator.DEFAULT_SMTP_TIMEOUT, configurator.getTimeout());
    }

    @Test
    public void getJson() throws Exception {

        String host = "json_host";
        int port = 123;
        int connectTimeout = 10;
        int timeout = 11;

        config.put(DefaultMailConfigurator.CONFIG_FIELD_HOST, host)
                .put(DefaultMailConfigurator.CONFIG_FIELD_PORT, port)
                .put(DefaultMailConfigurator.CONFIG_FIELD_SMTP_CONNECT_TIMEOUT, connectTimeout)
                .put(DefaultMailConfigurator.CONFIG_FIELD_SMTP_TIMEOUT, timeout);

        DefaultMailConfigurator configurator = createInstance();

        assertEquals(host, configurator.getHost());
        assertEquals(port, configurator.getPort());
        assertEquals(connectTimeout, configurator.getConnectTimeout());
        assertEquals(timeout, configurator.getTimeout());

    }

    @Test
    public void getEnv() throws Exception {

        String host = "env_host";
        int port = 124;
        int connectTimeout = 20;
        int timeout = 21;

        envVars.set(DefaultMailConfigurator.ENV_MAIL_HOST, host);
        envVars.set(DefaultMailConfigurator.ENV_MAIL_PORT, String.valueOf(port));
        envVars.set(DefaultMailConfigurator.ENV_MAIL_PORT, String.valueOf(port));
        envVars.set(DefaultMailConfigurator.ENV_MAIL_CONNECT_TIMEOUT, String.valueOf(connectTimeout));
        envVars.set(DefaultMailConfigurator.ENV_MAIL_TIMEOUT, String.valueOf(timeout));

        DefaultMailConfigurator configurator = createInstance();

        assertEquals(host, configurator.getHost());
        assertEquals(port, configurator.getPort());
        assertEquals(connectTimeout, configurator.getConnectTimeout());
        assertEquals(timeout, configurator.getTimeout());

    }

}