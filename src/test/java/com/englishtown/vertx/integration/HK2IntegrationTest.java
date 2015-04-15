package com.englishtown.vertx.integration;

import com.englishtown.vertx.hk2.HK2VertxBinder;
import com.englishtown.vertx.mail.MailService;
import com.englishtown.vertx.mail.hk2.ProxyMailBinder;
import io.vertx.test.core.VertxTestBase;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Test;

/**
 * Integration test for proxy hk2 binder
 */
public class HK2IntegrationTest extends VertxTestBase {

    private ServiceLocator locator;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        locator = ServiceLocatorFactory.getInstance().create(null);
        ServiceLocatorUtilities.bind(locator, new HK2VertxBinder(vertx), new ProxyMailBinder());

    }

    @Test
    public void testResolve() {

        MailService service = locator.getService(MailService.class, "proxy");
        assertEquals("MailServiceVertxEBProxy", service.getClass().getSimpleName());

    }
}
