package com.englishtown.vertx.impl;

import com.englishtown.vertx.mail.ContentType;
import com.englishtown.vertx.mail.MailService;
import com.englishtown.vertx.mail.SendOptions;
import com.englishtown.vertx.mail.TransportDelegate;
import com.englishtown.vertx.mail.impl.DefaultMailService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.mail.Message;
import javax.mail.Session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link com.englishtown.vertx.mail.impl.DefaultMailService}
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultMailServiceTest {

    private static final String FROM_ADDRESS = "integration_test@test.com";
    private static final String TO_ADDRESS = "test@test.com";
    private static final String CC_ADDRESS1 = "cctest1@test.com";
    private static final String CC_ADDRESS2 = "cctest2@test.com";
    private static final String SUBJECT = "Test Message";
    private static final String BODY = "This is a message from testSendingEmailSuccessfully";

    private static final String TEST_HOST = "test.mail.com";
    private static final int TEST_PORT = 12321;

    private static final int TEST_CONNECT_TIMEOUT = 20000;
    private static final int TEST_READ_TIMEOUT = 40000;

    private MailService service;

    @Mock
    private Vertx vertx;

    @Mock
    private Context context;

    @Mock
    private TransportDelegate transportDelegate;

    @Mock
    private Handler<AsyncResult<Void>> handler;

    @Captor
    private ArgumentCaptor<AsyncResult<Void>> asyncResultArgumentCaptor;

    @Captor
    ArgumentCaptor<Message> messageArgumentCaptor;

    @Before
    public void setUp() {
        when(vertx.getOrCreateContext()).thenReturn(context);
        when(context.config()).thenReturn(
                new JsonObject()
                        .put(DefaultMailService.CONFIG_FIELD_HOST, TEST_HOST)
                        .put(DefaultMailService.CONFIG_FIELD_PORT, TEST_PORT)
                        .put(DefaultMailService.CONFIG_FIELD_SMTP_CONNECT_TIMEOUT, TEST_CONNECT_TIMEOUT)
                        .put(DefaultMailService.CONFIG_FIELD_SMTP_READ_TIMEOUT, TEST_READ_TIMEOUT)
        );

        service = new DefaultMailService(vertx, transportDelegate);
        service.start();
    }

    @Test
    public void testSend_With_No_From_Address() {
        SendOptions options = new SendOptions().setFrom("");
        service.send(options, handler);

        verify(handler).handle(asyncResultArgumentCaptor.capture());
        AsyncResult<Void> asyncResult = asyncResultArgumentCaptor.getValue();
        assertTrue(asyncResult.failed());
        assertEquals("from eamil address must be specified", asyncResult.cause().getMessage());
    }

    @Test
    public void testSend_With_Invalid_None_Of_To_Cc_Bcc() {
        SendOptions options = new SendOptions().setFrom("from@something.com");
        service.send(options, handler);

        verify(handler).handle(asyncResultArgumentCaptor.capture());
        AsyncResult<Void> asyncResult = asyncResultArgumentCaptor.getValue();
        assertTrue(asyncResult.failed());
        assertEquals("At least one to/cc/bcc address must be specified", asyncResult.cause().getMessage());
    }

    @Test
    public void testSend_With_Invalid_Email_Address() {
        SendOptions options = new SendOptions().setFrom("g.com");
        service.send(options, handler);

        verify(handler).handle(asyncResultArgumentCaptor.capture());
        AsyncResult<Void> asyncResult = asyncResultArgumentCaptor.getValue();
        assertTrue(asyncResult.failed());
    }

    @Test
    public void testSend_Success() throws Exception {
        SendOptions options = new SendOptions()
                .setFrom(FROM_ADDRESS)
                .addTo(TO_ADDRESS)
                .addCc(CC_ADDRESS1)
                .addCc(CC_ADDRESS2)
                .setSubject(SUBJECT)
                .setContentType(ContentType.TEXT_HTML)
                .setBody(BODY);

        service.send(options, handler);

        // Verify mime message
        verify(transportDelegate).send(messageArgumentCaptor.capture());
        Message msg = messageArgumentCaptor.getValue();
        // msg.saveChanges() is called from Transport.send(msg)
        msg.saveChanges();
        assertEquals(FROM_ADDRESS, msg.getFrom()[0].toString());
        assertEquals(TO_ADDRESS, msg.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals(CC_ADDRESS1, msg.getRecipients(Message.RecipientType.CC)[0].toString());
        assertEquals(CC_ADDRESS2, msg.getRecipients(Message.RecipientType.CC)[1].toString());
        assertEquals(SUBJECT, msg.getSubject());
        assertEquals("text/html; charset=UTF-8", msg.getContentType());
        assertEquals(BODY, msg.getContent());

        // Verify mail session
        Session session = msg.getSession();
        assertEquals(DefaultMailService.SMTP, session.getProperty(DefaultMailService.MAIL_TRANSPORT_PROTOCOL_NAME));
        assertEquals(TEST_HOST, session.getProperty(DefaultMailService.MAIL_SMTP_HOST_NAME));
        assertEquals(String.valueOf(TEST_PORT), session.getProperty(DefaultMailService.MAIL_SMTP_PORT_NAME));
        assertEquals(String.valueOf(TEST_CONNECT_TIMEOUT), session.getProperty(DefaultMailService.MAIL_SMTP_CONNECT_TIMEOUT));
        assertEquals(String.valueOf(TEST_READ_TIMEOUT), session.getProperty(DefaultMailService.MAIL_SMTP_READ_TIMEOUT));

        // Verify async handler succeed
        verify(handler).handle(asyncResultArgumentCaptor.capture());
        assertTrue(asyncResultArgumentCaptor.getValue().succeeded());
    }
}

