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

import static com.englishtown.vertx.mail.impl.DefaultMailConfigurator.*;
import static org.junit.Assert.*;
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

        assertEquals(DEFAULT_TRANSPORT_PROTOCOL, configurator.getTransportProtocol());
        assertEquals(DEFAULT_HOST, configurator.getHost());
        assertEquals(DEFAULT_PORT, configurator.getPort());
        assertEquals(DEFAULT_SMTP_CONNECT_TIMEOUT, configurator.getConnectTimeout());
        assertEquals(DEFAULT_SMTP_TIMEOUT, configurator.getTimeout());
        assertNull(configurator.getUsername());
        assertNull(configurator.getPassword());
        assertFalse(configurator.getStartTlsEnable());
        assertFalse(configurator.getStartTlsRequired());
        assertFalse(configurator.isDebug());

    }

    @Test
    public void getJson() throws Exception {

        String protocol = "smtps";
        String host = "json_host";
        int port = 123;
        int connectTimeout = 10;
        int timeout = 11;
        String username = "json_user";
        String password = "json_password";
        boolean startTlsEnable = true;
        boolean startTlsRequired = true;
        boolean debug = true;

        config
                .put(CONFIG_FIELD_TRANSPORT_PROTOCOL, protocol)
                .put(CONFIG_FIELD_HOST, host)
                .put(CONFIG_FIELD_PORT, port)
                .put(CONFIG_FIELD_CONNECT_TIMEOUT, connectTimeout)
                .put(CONFIG_FIELD_TIMEOUT, timeout)
                .put(CONFIG_FIELD_USERNAME, username)
                .put(CONFIG_FIELD_PASSWORD, password)
                .put(CONFIG_FIELD_STARTTLS_ENABLE, startTlsEnable)
                .put(CONFIG_FIELD_STARTTLS_REQUIRED, startTlsRequired)
                .put(CONFIG_FIELD_DEBUG, debug);

        DefaultMailConfigurator configurator = createInstance();

        assertEquals(protocol, configurator.getTransportProtocol());
        assertEquals(host, configurator.getHost());
        assertEquals(port, configurator.getPort());
        assertEquals(connectTimeout, configurator.getConnectTimeout());
        assertEquals(timeout, configurator.getTimeout());
        assertEquals(username, configurator.getUsername());
        assertEquals(password, configurator.getPassword());
        assertEquals(startTlsEnable, configurator.getStartTlsEnable());
        assertEquals(startTlsRequired, configurator.getStartTlsRequired());
        assertEquals(debug, configurator.isDebug());

    }

    @Test
    public void getEnv() throws Exception {

        String protocol = "smtps";
        String host = "env_host";
        int port = 124;
        int connectTimeout = 20;
        int timeout = 21;
        String username = "env_user";
        String password = "env_password";
        boolean startTlsEnable = true;
        boolean startTlsRequired = true;
        boolean debug = true;

        envVars.set(ENV_MAIL_TRANSPORT_PROTOCOL, protocol);
        envVars.set(ENV_MAIL_HOST, host);
        envVars.set(ENV_MAIL_PORT, String.valueOf(port));
        envVars.set(ENV_MAIL_CONNECT_TIMEOUT, String.valueOf(connectTimeout));
        envVars.set(ENV_MAIL_TIMEOUT, String.valueOf(timeout));
        envVars.set(ENV_MAIL_USERNAME, username);
        envVars.set(ENV_MAIL_PASSWORD, password);
        envVars.set(ENV_MAIL_STARTTLS_ENABLE, String.valueOf(startTlsEnable));
        envVars.set(ENV_MAIL_STARTTLS_REQUIRED, String.valueOf(startTlsRequired));
        envVars.set(ENV_MAIL_DEBUG, String.valueOf(debug));

        DefaultMailConfigurator configurator = createInstance();

        assertEquals(protocol, configurator.getTransportProtocol());
        assertEquals(host, configurator.getHost());
        assertEquals(port, configurator.getPort());
        assertEquals(connectTimeout, configurator.getConnectTimeout());
        assertEquals(timeout, configurator.getTimeout());
        assertEquals(username, configurator.getUsername());
        assertEquals(password, configurator.getPassword());
        assertEquals(startTlsEnable, configurator.getStartTlsEnable());
        assertEquals(startTlsRequired, configurator.getStartTlsRequired());
        assertEquals(debug, configurator.isDebug());

    }

}