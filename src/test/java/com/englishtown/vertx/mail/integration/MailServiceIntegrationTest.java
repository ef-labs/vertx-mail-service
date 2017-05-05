package com.englishtown.vertx.mail.integration;

import com.englishtown.vertx.mail.ContentType;
import com.englishtown.vertx.mail.MailService;
import com.englishtown.vertx.mail.SendOptions;
import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;
import org.junit.Rule;
import org.junit.Test;

import javax.mail.internet.MimeMessage;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Mail service integration tests
 */
public class MailServiceIntegrationTest extends VertxTestBase {

    private static final String MAILER_ADDRESS = "et.mailer";
    private static final String CONFIG_FIELD_PORT = "port";

    private static final int DEPLOY_TIMEOUT_SECONDS = 2;

    private static final String FROM_ADDRESS = "integration_test@test.com";
    private static final String TO_ADDRESS = "test@test.com";
    private static final String CC_ADDRESS1 = "cctest1@test.com";
    private static final String CC_ADDRESS2 = "cctest2@test.com";
    private static final String BCC_ADDRESS1 = "bcctest1@test.com";
    private static final String BCC_ADDRESS2 = "bcctest2@test.com";
    private static final String SUBJECT = "Test Message";
    private static final String BODY = "This is a message from testSendingEmailSuccessfully";

    private MailService service;

    @Rule
    public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);


    @Override
    public void setUp() throws Exception {
        super.setUp();

        service = MailService.createEventBusProxy(vertx, MAILER_ADDRESS);

        CountDownLatch latch = new CountDownLatch(1);

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put(CONFIG_FIELD_PORT, ServerSetupTest.SMTP.getPort()));

        vertx.deployVerticle("service:com.englishtown.vertx.vertx-mail-service", options, result -> {
            if (result.failed()) {
                result.cause().printStackTrace();
                fail();
            }
            latch.countDown();
        });

        latch.await(DEPLOY_TIMEOUT_SECONDS, TimeUnit.SECONDS);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        service.stop();
    }

    @Test
    public void testSendingEmailSuccessfully() throws Exception {

        SendOptions options = new SendOptions()
                .setFrom(FROM_ADDRESS)
                .addTo(TO_ADDRESS)
                .addCc(CC_ADDRESS1)
                .addCc(CC_ADDRESS2)
                .addBcc(BCC_ADDRESS1)
                .addBcc(BCC_ADDRESS2)
                .setSubject(SUBJECT)
                .setContentType(ContentType.TEXT_PLAIN)
                .setBody(BODY);

        service.send(options, result -> {
            assertTrue(result.succeeded());

            vertx.setTimer(500, id -> {
                try {
                    MimeMessage[] messages = greenMail.getReceivedMessages();

                    // Assert that the email sent is what we expected
                    assertEquals(5, messages.length);
                    MimeMessage message = messages[0];

                    assertEquals(FROM_ADDRESS, message.getFrom()[0].toString());
                    assertEquals(CC_ADDRESS1 + ", " + CC_ADDRESS2, message.getHeader("Cc")[0]);
                    assertNull(message.getHeader("Bcc"));
                    assertEquals(SUBJECT, message.getSubject());
                    assertEquals("7bit", message.getHeader("Content-Transfer-Encoding")[0]);
                    assertEquals(BODY + "\r\n", message.getContent());

                    testComplete();

                } catch (Throwable t) {
                    t.printStackTrace();
                    fail(t);
                }
            });
        });

        await();
    }

    @Test
    public void testSendingEmailSuccessfully_With_Double_Byte_Chars() throws Exception {
        String subject = "嗨";
        String body = "你好";

        SendOptions options = new SendOptions()
                .setFrom(FROM_ADDRESS)
                .addTo(TO_ADDRESS)
                .setSubject(subject)
                .setContentType(ContentType.TEXT_PLAIN)
                .setBody(body);

        service.send(options, result -> {
            assertTrue(result.succeeded());

            vertx.setTimer(500, id -> {
                try {
                    MimeMessage[] messages = greenMail.getReceivedMessages();

                    // Assert that the email sent is what we expected
                    assertEquals(1, messages.length);
                    MimeMessage message = messages[0];

                    assertEquals(FROM_ADDRESS, message.getFrom()[0].toString());
                    assertEquals(subject, message.getSubject());
                    assertEquals("base64", message.getHeader("Content-Transfer-Encoding")[0]);
                    assertEquals(body, message.getContent());

                    testComplete();

                } catch (Throwable t) {
                    t.printStackTrace();
                    fail(t);
                }
            });
        });

        await();
    }

    @Test
    public void testSendingEmailFailed() throws Exception {
        SendOptions options = new SendOptions();

        service.send(options, result -> {
            assertTrue(result.failed());
            testComplete();
        });

        await();
    }

}
