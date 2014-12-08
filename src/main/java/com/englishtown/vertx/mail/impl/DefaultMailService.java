package com.englishtown.vertx.mail.impl;

import com.englishtown.vertx.mail.MailService;
import com.englishtown.vertx.mail.SendOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
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
    private static final String SMTP = "smtp";

    private static final String MAIL_TRANSPORT_PROTOCOL_NAME = "mail.transport.protocol";
    private static final String MAIL_SMTP_HOST_NAME = "mail.smtp.host";
    private static final String MAIL_SMTP_PORT_NAME = "mail.smtp.port";

    private static final String CONFIG_FIELD_HOST = "host";
    private static final String CONFIG_FIELD_PORT = "port";

    private Session session;
    private final Vertx vertx;

    @Inject
    public DefaultMailService(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void start() {
        initializeMailSession();
    }

    @Override
    public void stop() {

    }

    @Override
    public void send(SendOptions options, Handler<AsyncResult<Void>> resultHandler) {
        try {
            options.validate();

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(options.getFrom()));
            msg.setRecipients(Message.RecipientType.TO, parseMultipleAddresses("to", options.getTo()));
            msg.setRecipients(Message.RecipientType.CC, parseMultipleAddresses("cc", options.getCc()));
            msg.setRecipients(Message.RecipientType.BCC, parseMultipleAddresses("bcc", options.getBcc()));
            msg.setSubject(options.getSubject());
            msg.setContent(options.getBody(), options.getContentType().getType());
            msg.setSentDate(new Date());

            Transport.send(msg);
            resultHandler.handle(Future.succeededFuture());

        } catch (Throwable t) {
            resultHandler.handle(Future.failedFuture(t));
        }
    }

    private InternetAddress[] parseMultipleAddresses(String field, List<String> addresses) throws AddressException {
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }

        InternetAddress[] result = new InternetAddress[addresses.size()];
        for (int i = 0; i < addresses.size(); i++) {
            result[i] = new InternetAddress(addresses.get(i));
        }
        return result;
    }

    private void initializeMailSession() {
        JsonObject config = vertx.getOrCreateContext().config();

        Properties props = new Properties();
        props.put(MAIL_TRANSPORT_PROTOCOL_NAME, SMTP);
        props.put(MAIL_SMTP_HOST_NAME, config.getString(CONFIG_FIELD_HOST, DEFAULT_HOST));
        props.put(MAIL_SMTP_PORT_NAME, Integer.toString(config.getInteger(CONFIG_FIELD_PORT, DEFAULT_PORT)));

        session = Session.getInstance(props);
    }

}
