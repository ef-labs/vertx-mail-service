package com.englishtown.vertx.integration;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import com.englishtown.vertx.mail.ContentType;
import com.englishtown.vertx.mail.MailService;
import com.englishtown.vertx.mail.SendOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Mail service integration tests
 */
public class MailServiceIntegrationTest extends VertxTestBase {

    private static final String MAILER_ADDRESS = "et.mailer";
    private static final int SMTP_PORT = 18271;
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
    private SimpleSmtpServer smtpServer;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        service = MailService.createEventBusProxy(vertx, MAILER_ADDRESS);
        smtpServer = SimpleSmtpServer.start(SMTP_PORT);

        CountDownLatch latch = new CountDownLatch(1);

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put(CONFIG_FIELD_PORT, SMTP_PORT));

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
        if (smtpServer != null) {
            smtpServer.stop();
        }
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

            // Assert that the email sent is what we expected
            assertEquals(1, smtpServer.getReceivedEmailSize());
            SmtpMessage receivedMessage = (SmtpMessage) smtpServer.getReceivedEmail().next();

            assertEquals(FROM_ADDRESS, receivedMessage.getHeaderValue("From"));
            assertEquals(CC_ADDRESS1 + ", " + CC_ADDRESS2, receivedMessage.getHeaderValue("Cc"));
            assertEquals(null, receivedMessage.getHeaderValue("Bcc"));
            assertEquals(SUBJECT, receivedMessage.getHeaderValue("Subject"));
            assertEquals("7bit", receivedMessage.getHeaderValue("Content-Transfer-Encoding"));
            assertEquals(BODY, receivedMessage.getBody());

            testComplete();
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
                .addCc(CC_ADDRESS1)
                .addCc(CC_ADDRESS2)
                .addBcc(BCC_ADDRESS1)
                .addBcc(BCC_ADDRESS2)
                .setSubject(subject)
                .setContentType(ContentType.TEXT_PLAIN)
                .setBody(body);

        service.send(options, result -> {
            assertTrue(result.succeeded());

            // Assert that the email sent is what we expected
            assertEquals(1, smtpServer.getReceivedEmailSize());
            SmtpMessage receivedMessage = (SmtpMessage) smtpServer.getReceivedEmail().next();

            assertEquals(FROM_ADDRESS, receivedMessage.getHeaderValue("From"));
            assertEquals(CC_ADDRESS1 + ", " + CC_ADDRESS2, receivedMessage.getHeaderValue("Cc"));
            assertEquals(null, receivedMessage.getHeaderValue("Bcc"));
            assertTrue(receivedMessage.getHeaderValue("Subject").startsWith("=?UTF-8?B?"));
            assertEquals(subject, new String(Base64.getDecoder().decode(receivedMessage.getHeaderValue("Subject").substring(10, 14))));
            assertEquals("base64", receivedMessage.getHeaderValue("Content-Transfer-Encoding"));
            assertEquals(body, new String(Base64.getDecoder().decode(receivedMessage.getBody())));

            testComplete();
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
