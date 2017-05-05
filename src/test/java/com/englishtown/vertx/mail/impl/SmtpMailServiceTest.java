package com.englishtown.vertx.mail.impl;

import com.englishtown.vertx.mail.ContentType;
import com.englishtown.vertx.mail.MailConfigurator;
import com.englishtown.vertx.mail.SendOptions;
import com.englishtown.vertx.mail.SessionFactory;
import com.englishtown.vertx.mail.impl.DefaultMailConfigurator;
import com.englishtown.vertx.mail.impl.SmtpMailService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;

import java.util.Properties;

import static com.englishtown.vertx.mail.impl.DefaultMailConfigurator.DEFAULT_TRANSPORT_PROTOCOL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SmtpMailService}
 */
public class SmtpMailServiceTest {

    private static final String FROM_ADDRESS = "integration_test@test.com";
    private static final String TO_ADDRESS = "test@test.com";
    private static final String CC_ADDRESS1 = "cctest1@test.com";
    private static final String CC_ADDRESS2 = "cctest2@test.com";
    private static final String SUBJECT = "Test Message";
    private static final String BODY = "This is a message from testSendingEmailSuccessfully";

    private static final String TEST_HOST = "test.mail.com";
    private static final int TEST_PORT = 12321;

    private static final int TEST_CONNECT_TIMEOUT = 20000;
    private static final int TEST_TIMEOUT = 40000;
    private static final String TEST_TRANSPORT_PROTOCOL = "smtps";

    private SmtpMailService service;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private MailConfigurator configurator;
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Handler<AsyncResult<Void>> handler;
    @Mock
    private Session session;
    @Mock
    private Transport transport;

    @Captor
    private ArgumentCaptor<Properties> propsArgumentCaptor;
    @Captor
    private ArgumentCaptor<AsyncResult<Void>> asyncResultArgumentCaptor;
    @Captor
    private ArgumentCaptor<Message> messageArgumentCaptor;

    @Before
    public void setUp() throws Exception {

        when(configurator.getTransportProtocol()).thenReturn(TEST_TRANSPORT_PROTOCOL);
        when(configurator.getHost()).thenReturn(TEST_HOST);
        when(configurator.getPort()).thenReturn(TEST_PORT);
        when(configurator.getConnectTimeout()).thenReturn(TEST_CONNECT_TIMEOUT);
        when(configurator.getTimeout()).thenReturn(TEST_TIMEOUT);

        when(sessionFactory.getInstance(any())).thenReturn(session);
        when(session.getTransport()).thenReturn(transport);

        service = new SmtpMailService(configurator, sessionFactory);
        service.start();

    }

    @Test
    public void testInitSession() throws Exception {

        verify(session).getTransport();
        verify(transport).connect();

        verify(sessionFactory).getInstance(propsArgumentCaptor.capture());
        Properties props = propsArgumentCaptor.getValue();

        // Verify mail session
        assertEquals(TEST_TRANSPORT_PROTOCOL, props.getProperty(SmtpMailService.MAIL_TRANSPORT_PROTOCOL));
        assertEquals(TEST_HOST, props.getProperty(SmtpMailService.MAIL_SMTP_HOST));
        assertEquals(String.valueOf(TEST_PORT), props.getProperty(SmtpMailService.MAIL_SMTP_PORT));
        assertEquals(String.valueOf(TEST_CONNECT_TIMEOUT), props.getProperty(SmtpMailService.MAIL_SMTP_CONNECT_TIMEOUT));
        assertEquals(String.valueOf(TEST_TIMEOUT), props.getProperty(SmtpMailService.MAIL_SMTP_TIMEOUT));
        assertEquals(String.valueOf(false), props.getProperty(SmtpMailService.MAIL_SMTP_STARTTLS_ENABLE));
        assertEquals(String.valueOf(false), props.getProperty(SmtpMailService.MAIL_SMTP_STARTTLS_REQUIRED));

    }

    @Test
    public void testInitSession_Auth() throws Exception {

        String username = "test_user";
        String password = "test_password";

        when(configurator.getUsername()).thenReturn(username);
        when(configurator.getPassword()).thenReturn(password);

        service = new SmtpMailService(configurator, sessionFactory);
        service.start();

        verify(transport).connect(eq(username), eq(password));

    }

    @Test
    public void testSend_With_No_From_Address() {
        SendOptions options = new SendOptions().setFrom("");
        service.send(options, handler);

        verify(handler).handle(asyncResultArgumentCaptor.capture());
        AsyncResult<Void> asyncResult = asyncResultArgumentCaptor.getValue();
        assertTrue(asyncResult.failed());
        assertEquals("from email address must be specified", asyncResult.cause().getMessage());
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

        verify(sessionFactory).getInstance(propsArgumentCaptor.capture());
        Properties props = propsArgumentCaptor.getValue();
        when(session.getProperties()).thenReturn(props);

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
        verify(transport).sendMessage(messageArgumentCaptor.capture(), any());
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

        // Verify async handler succeed
        verify(handler).handle(asyncResultArgumentCaptor.capture());
        assertTrue(asyncResultArgumentCaptor.getValue().succeeded());
    }

    @Test
    public void testStop() throws Exception {
        service.stop();
        verify(transport).close();
    }

}

