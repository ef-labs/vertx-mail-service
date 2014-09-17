package com.englishtown.vertx.integration;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import com.englishtown.vertx.Mailer;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import static org.vertx.testtools.VertxAssert.assertEquals;

/**
 */
public class BasicIntegrationTest extends TestVerticle {

    @Override
    public void start(Future<Void> startedResult) {
        initialize();

        container.deployVerticle(Mailer.class.getName(), new JsonObject().putNumber("port", 18271), asyncResult -> {
            if (asyncResult.succeeded()) {
                startTests();
                startedResult.setResult(null);
            } else {
                startedResult.setFailure(asyncResult.cause());
            }
        });
    }

    @Test
    public void testSendingEmailSuccessfully() throws Exception {
        SimpleSmtpServer server = SimpleSmtpServer.start(18271);

        JsonObject message = new JsonObject()
                .putString("from", "integration_test@test.com")
                .putString("to", "test@test.com")
                .putArray("cc", new JsonArray().add("cctest1@test.com").add("cctest2@test.com"))
                .putString("subject", "Test Message")
                .putString("body", "This is a message from testSendingEmailSuccessfully");

        vertx.eventBus().send("et.mailer", message, (Message<JsonObject> reply) -> {
            String status = reply.body().getString("status");
            assertEquals("ok", status);

            server.stop();

            // Assert that the email sent is what we expected
            assertEquals(1, server.getReceivedEmailSize());
            SmtpMessage receivedMessage = (SmtpMessage) server.getReceivedEmail().next();

            assertEquals("integration_test@test.com", receivedMessage.getHeaderValue("From"));
            assertEquals("cctest1@test.com, cctest2@test.com", receivedMessage.getHeaderValue("Cc"));
            assertEquals(null, receivedMessage.getHeaderValue("Bcc"));
            assertEquals("Test Message", receivedMessage.getHeaderValue("Subject"));
            assertEquals("This is a message from testSendingEmailSuccessfully", receivedMessage.getBody());

            VertxAssert.testComplete();
        });
    }
}
