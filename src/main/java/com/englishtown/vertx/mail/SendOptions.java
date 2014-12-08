package com.englishtown.vertx.mail;

import io.vertx.codegen.annotations.Options;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Options for sending email
 */
@Options
public class SendOptions {

    private String from;
    private List<String> to = new ArrayList<>();
    private List<String> cc = new ArrayList<>();
    private List<String> bcc = new ArrayList<>();
    private String subject;
    private ContentType contentType = ContentType.TEXT_PLAIN;
    private String body;

    public static final String JSON_FIELD_FROM = "from";
    public static final String JSON_FIELD_TO = "to";
    public static final String JSON_FIELD_CC = "cc";
    public static final String JSON_FIELD_BCC = "bcc";
    public static final String JSON_FIELD_SUBJECT = "subject";
    public static final String JSON_FIELD_CONTENT_TYPE = "contentType";
    public static final String JSON_FIELD_BODY = "body";

    public SendOptions() {
    }

    public SendOptions(SendOptions other) {
        from = other.from;
        to = other.to;
        cc = other.cc;
        bcc = other.bcc;
        subject = other.subject;
        contentType = other.contentType;
        body = other.body;
    }

    public SendOptions(JsonObject json) {
        from = json.getString(JSON_FIELD_FROM);
        to = getStringListFromJsonArray(json.getJsonArray(JSON_FIELD_TO));
        cc = getStringListFromJsonArray(json.getJsonArray(JSON_FIELD_CC));
        bcc = getStringListFromJsonArray(json.getJsonArray(JSON_FIELD_BCC));
        subject = json.getString((JSON_FIELD_SUBJECT));

        String type = json.getString(JSON_FIELD_CONTENT_TYPE);
        if (type != null) {
            contentType = ContentType.fromString(type);
        }

        body = json.getString(JSON_FIELD_BODY);
    }

    public String getFrom() {
        return from;
    }

    public SendOptions setFrom(String from) {
        this.from = from;
        return this;
    }

    public List<String> getTo() {
        return to;
    }

    public SendOptions addTo(String to) {
        this.to.add(to);
        return this;
    }

    public List<String> getCc() {
        return cc;
    }

    public SendOptions addCc(String cc) {
        this.cc.add(cc);
        return this;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public SendOptions addBcc(String bcc) {
        this.bcc.add(bcc);
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public SendOptions setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public SendOptions setContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getBody() {
        return body;
    }

    public SendOptions setBody(String body) {
        this.body = body;
        return this;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        if (from != null) {
            json.put(JSON_FIELD_FROM, from);
        }
        if (!to.isEmpty()) {
            json.put(JSON_FIELD_TO, new JsonArray(to));
        }
        if (!cc.isEmpty()) {
            json.put(JSON_FIELD_CC, new JsonArray(cc));
        }
        if (!bcc.isEmpty()) {
            json.put(JSON_FIELD_BCC, new JsonArray(bcc));
        }
        if (subject != null) {
            json.put(JSON_FIELD_SUBJECT, subject);
        }
        if (contentType != null) {
            json.put(JSON_FIELD_CONTENT_TYPE, contentType.getType());
        }
        if (body != null) {
            json.put(JSON_FIELD_BODY, body);
        }

        return json;
    }

    private List<String> getStringListFromJsonArray(JsonArray source) {
        List<String> result = new ArrayList<>();

        if (source != null) {
            for (int i = 0; i < source.size(); i++) {
                result.add(source.getString(i));
            }
        }

        return result;
    }
}
