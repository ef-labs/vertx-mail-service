package com.englishtown.vertx;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static javax.mail.Message.RecipientType.*;

/**
 */
public class Mailer extends BusModBase implements Handler<Message<JsonObject>> {

    private static final String DEFAULT_ADDRESS = "et.mailer";
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 25;
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";

    private Logger logger = LoggerFactory.getLogger(Mailer.class);
    private Session session;

    @Override
    public void start() {
        super.start();

        JsonObject config = container.config();

        // Configure mailer here
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", config.getString("host", DEFAULT_HOST));
        props.put("mail.smtp.port", Integer.toString(config.getInteger("port", DEFAULT_PORT)));

        session = Session.getInstance(props);

        // Now start listening
        eb.registerHandler(config.getString("address", DEFAULT_ADDRESS), this);
  }

    @Override
    public void handle(Message<JsonObject> message) {
        try {
            JsonObject jsonMessage = message.body();
            javax.mail.Message msg = new MimeMessage(session);

            msg.setFrom(parseAddress(jsonMessage, "from", true));
            msg.setRecipients(TO, parseMultiAddress(jsonMessage, "to", true));
            msg.setRecipients(CC, parseMultiAddress(jsonMessage, "cc", false));
            msg.setRecipients(BCC, parseMultiAddress(jsonMessage, "bcc", false));
            msg.setSubject(jsonMessage.getString("subject"));
            msg.setContent(jsonMessage.getString("body"), jsonMessage.getString("content_type", DEFAULT_CONTENT_TYPE));
            msg.setSentDate(new Date());

            Transport.send(msg);

            sendOK(message);
        } catch (Exception e) {
            sendError(message, e.getMessage());
        }

    }

    private InternetAddress parseAddress(JsonObject jsonMessage, String field, boolean mandatory) throws AddressException {
        String address = jsonMessage.getString(field);
        if (Strings.isNullOrEmpty(address)) {
            if (mandatory) throw new AddressException("Mandatory field " + field + " is missing");
            return null;
        }

        return new InternetAddress(address);
    }

    private InternetAddress[] parseMultiAddress(JsonObject jsonMessage, String field, boolean mandatory) throws AddressException {
        Object address = jsonMessage.getField(field);

        if (address instanceof String) {
            return new InternetAddress[] {parseAddress(jsonMessage, field, mandatory)};
        }

        if (!(address instanceof JsonArray)) {
            if (mandatory) throw new AddressException("Mandatory field " + field + " is missing");
            return null;
        }

        JsonArray addresses = (JsonArray) address;
        InternetAddress[] internetAddresses = new InternetAddress[addresses.size()];

        for (int i = 0; i < addresses.size(); i++) {
            internetAddresses[i] = new InternetAddress((String) addresses.get(i));
        }

        return internetAddresses;
    }
}
