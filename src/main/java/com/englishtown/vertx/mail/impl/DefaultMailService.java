package com.englishtown.vertx.mail.impl;

import com.englishtown.vertx.mail.MailService;
import com.englishtown.vertx.mail.SendOptions;
import com.englishtown.vertx.mail.TransportDelegate;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Default implementation of {@link com.englishtown.vertx.mail.MailService}
 */
public class DefaultMailService implements MailService {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 25;
    public static final String SMTP = "smtp";
    private static final int DEFAULT_SMTP_CONNECT_TIMEOUT = 10000;
    private static final int DEFAULT_SMTP_TIMEOUT = 30000;
    public static final String CHARSET_UTF8 = "UTF-8";

    public static final String MAIL_TRANSPORT_PROTOCOL_NAME = "mail.transport.protocol";
    public static final String MAIL_SMTP_HOST_NAME = "mail.smtp.host";
    public static final String MAIL_SMTP_PORT_NAME = "mail.smtp.port";
    public static final String MAIL_SMTP_CONNECT_TIMEOUT = "mail.smtp.connectiontimeout";
    public static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";

    public static final String CONFIG_FIELD_HOST = "host";
    public static final String CONFIG_FIELD_PORT = "port";
    public static final String CONFIG_FIELD_SMTP_CONNECT_TIMEOUT = "smtp_connect_timeout";
    public static final String CONFIG_FIELD_SMTP_TIMEOUT = "smtp_timeout";

    private Session session;
    private final Vertx vertx;
    private final TransportDelegate transportDelegate;

    @Inject
    public DefaultMailService(Vertx vertx, TransportDelegate transportDelegate) {
        this.vertx = vertx;
        this.transportDelegate = transportDelegate;
    }

    @Override
    public void start() {
        initSession();
    }

    @Override
    public void stop() {

    }

    @Override
    public void send(SendOptions options, Handler<AsyncResult<Void>> resultHandler) {
        try {
            validate(options);
            Message msg = createMessage(options);
            transportDelegate.send(msg);
            resultHandler.handle(Future.succeededFuture());

        } catch (Throwable t) {
            resultHandler.handle(Future.failedFuture(t));
        }
    }

    private void initSession() {
        JsonObject config = vertx.getOrCreateContext().config();

        Properties props = new Properties();
        props.put(MAIL_TRANSPORT_PROTOCOL_NAME, SMTP);
        props.put(MAIL_SMTP_HOST_NAME, config.getString(CONFIG_FIELD_HOST, DEFAULT_HOST));
        props.put(MAIL_SMTP_PORT_NAME, Integer.toString(config.getInteger(CONFIG_FIELD_PORT, DEFAULT_PORT)));
        props.put(MAIL_SMTP_CONNECT_TIMEOUT, Integer.toString(config.getInteger(CONFIG_FIELD_SMTP_CONNECT_TIMEOUT, DEFAULT_SMTP_CONNECT_TIMEOUT)));
        props.put(MAIL_SMTP_TIMEOUT, Integer.toString(config.getInteger(CONFIG_FIELD_SMTP_TIMEOUT, DEFAULT_SMTP_TIMEOUT)));

        session = Session.getInstance(props);
    }

    protected Message createMessage(SendOptions options) throws MessagingException {
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(options.getFrom()));
        msg.setRecipients(Message.RecipientType.TO, parseMultipleAddresses(options.getTo()));
        msg.setRecipients(Message.RecipientType.CC, parseMultipleAddresses(options.getCc()));
        msg.setRecipients(Message.RecipientType.BCC, parseMultipleAddresses(options.getBcc()));
        msg.setSubject(options.getSubject(), CHARSET_UTF8);
        msg.setText(options.getBody(), CHARSET_UTF8, options.getContentType().getSubType());
        msg.setSentDate(new Date());
        return msg;
    }

    /**
     * Validate SendOptions
     *
     * @param options
     * @throws java.lang.IllegalArgumentException
     */
    private void validate(SendOptions options) {
        if (options.getFrom() == null || options.getFrom().isEmpty()) {
            throw new IllegalArgumentException("from eamil address must be specified");
        }
        if ((options.getTo() == null || options.getTo().isEmpty())
                && (options.getCc() == null || options.getCc().isEmpty())
                && (options.getBcc() == null || options.getBcc().isEmpty())) {

            throw new IllegalArgumentException("At least one to/cc/bcc address must be specified");
        }
    }

    private InternetAddress[] parseMultipleAddresses(List<String> addresses) throws AddressException {
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }

        InternetAddress[] result = new InternetAddress[addresses.size()];
        for (int i = 0; i < addresses.size(); i++) {
            InternetAddress address = new InternetAddress(addresses.get(i));
            address.validate();
            result[i] = address;
        }
        return result;
    }
}
