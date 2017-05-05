package com.englishtown.vertx.mail.impl;

import org.junit.Before;
import org.junit.Test;

import javax.mail.Session;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link DefaultSessionFactoryTest}
 */
public class DefaultSessionFactoryTest {

    private DefaultSessionFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new DefaultSessionFactory();
    }

    @Test
    public void getInstance() throws Exception {
        Properties props = new Properties();
        Session session = factory.getInstance(props);
        assertNotNull(session);
    }

}