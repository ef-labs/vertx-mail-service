package com.englishtown.vertx.mail.impl;

import com.englishtown.vertx.mail.MailConfigurator;
import com.englishtown.vertx.mail.MailService;
import com.englishtown.vertx.mail.SendOptions;
import com.englishtown.vertx.mail.SessionFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
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
public class SmtpMailService implements MailService {

    public static final String CHARSET_UTF8 = "UTF-8";

    public static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    public static final String MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String MAIL_SMTP_PORT = "mail.smtp.port";
    public static final String MAIL_SMTP_CONNECT_TIMEOUT = "mail.smtp.connectiontimeout";
    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    public static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
    public static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String MAIL_SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";

    private Session session;
    private Transport transport;
    private final MailConfigurator configurator;
    private final SessionFactory sessionFactory;

    private static final Logger logger = LoggerFactory.getLogger(SmtpMailService.class);

    @Inject
    public SmtpMailService(MailConfigurator configurator, SessionFactory sessionFactory) {
        this.configurator = configurator;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void start() {
        try {
            initSession();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        if (transport != null) {
            try {
                transport.close();
            } catch (MessagingException e) {
                logger.warn("Error closing transport", e);
            }
            transport = null;
        }
    }

    @Override
    public void send(SendOptions options, Handler<AsyncResult<Void>> resultHandler) {
        try {
            validate(options);
            Message msg = createMessage(options);
            logger.trace("Sending message to {}", (Object[]) msg.getAllRecipients());
            if (!transport.isConnected()) {
                transport.connect();
            }
            transport.sendMessage(msg, msg.getAllRecipients());
            resultHandler.handle(Future.succeededFuture());

        } catch (Throwable t) {
            logger.warn("Error sending message", t);
            resultHandler.handle(Future.failedFuture(t));
        }
    }

    private void initSession() throws MessagingException {

        logger.info("Initializing mail session: protocol={} host={} port={}",
                configurator.getTransportProtocol(),
                configurator.getHost(),
                configurator.getPort());

        Properties props = new Properties();
        props.put(MAIL_TRANSPORT_PROTOCOL, configurator.getTransportProtocol());
        props.put(MAIL_SMTP_HOST, configurator.getHost());
        props.put(MAIL_SMTP_PORT, Integer.toString(configurator.getPort()));
        props.put(MAIL_SMTP_CONNECT_TIMEOUT, Integer.toString(configurator.getConnectTimeout()));
        props.put(MAIL_SMTP_TIMEOUT, Integer.toString(configurator.getTimeout()));
        props.put(MAIL_SMTP_STARTTLS_ENABLE, Boolean.toString(configurator.getStartTlsEnable()));
        props.put(MAIL_SMTP_STARTTLS_REQUIRED, Boolean.toString(configurator.getStartTlsRequired()));

        String username = configurator.getUsername();
        String password = configurator.getPassword();

        boolean auth = username != null &&
                username.length() > 0 &&
                password != null &&
                password.length() > 0;

        if (auth) {
            props.put(MAIL_SMTP_AUTH, Boolean.TRUE.toString());
        }

        session = sessionFactory.getInstance(props);

        if (configurator.isDebug()) {
            session.setDebug(true);
        }

        transport = session.getTransport();

        if (auth) {
            logger.info("Transport connecting with username {}", username);
            transport.connect(username, password);
        } else {
            transport.connect();
        }

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
            throw new IllegalArgumentException("from email address must be specified");
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
