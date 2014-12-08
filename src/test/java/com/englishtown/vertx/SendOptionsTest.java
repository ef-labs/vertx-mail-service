package com.englishtown.vertx;

import com.englishtown.vertx.mail.ContentType;
import com.englishtown.vertx.mail.SendOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for {@link SendOptionsTest}
 */
public class SendOptionsTest {

    private static final String FROM_ADDRESS = "integration_test@test.com";
    private static final String TO_ADDRESS = "test@test.com";
    private static final String CC_ADDRESS1 = "cctest1@test.com";
    private static final String BCC_ADDRESS1 = "bcctest1@test.com";
    private static final String SUBJECT = "Test Message";
    private static final String BODY = "This is a message from testSendingEmailSuccessfully";
    private static final ContentType CONTENT_TYPE = ContentType.TEXT_PLAIN;

    @Test
    public void testCopyFromOther() {
        SendOptions other = getSimpleOptions();
        SendOptions options = new SendOptions(other);

        assertNotNull(options);
        assertEquals(other.getFrom(), options.getFrom());
        assertEquals(other.getTo(), options.getTo());
        assertEquals(other.getCc(), options.getCc());
        assertEquals(other.getBcc(), options.getBcc());
        assertEquals(other.getSubject(), options.getSubject());
        assertEquals(other.getContentType(), options.getContentType());
        assertEquals(other.getBody(), options.getBody());
    }

    @Test
    public void testToJson() {
        SendOptions options = getSimpleOptions();
        JsonObject json = options.toJson();

        assertNotNull(json);
        assertEquals(options.getFrom(), json.getString(SendOptions.JSON_FIELD_FROM));
        assertJsonArrayEquals(options.getTo(), json.getJsonArray(SendOptions.JSON_FIELD_TO));
        assertJsonArrayEquals(options.getCc(), json.getJsonArray(SendOptions.JSON_FIELD_CC));
        assertJsonArrayEquals(options.getBcc(), json.getJsonArray(SendOptions.JSON_FIELD_BCC));
        assertEquals(options.getSubject(), json.getString(SendOptions.JSON_FIELD_SUBJECT));
        assertEquals(options.getContentType(), ContentType.fromString(json.getString(SendOptions.JSON_FIELD_CONTENT_TYPE)));
        assertEquals(options.getBody(), json.getString(SendOptions.JSON_FIELD_BODY));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_from_not_provided() {
        SendOptions options = new SendOptions()
                .addTo(TO_ADDRESS);
        options.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidate_to_not_provided() {
        SendOptions options = new SendOptions()
                .setFrom(FROM_ADDRESS);
        options.validate();
    }

    private void assertJsonArrayEquals(List<String> expected, JsonArray actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.getString(i));
        }
    }

    private SendOptions getSimpleOptions() {
        return new SendOptions()
                .setFrom(FROM_ADDRESS)
                .addTo(TO_ADDRESS)
                .addCc(CC_ADDRESS1)
                .addBcc(BCC_ADDRESS1)
                .setSubject(SUBJECT)
                .setContentType(CONTENT_TYPE)
                .setBody(BODY);
    }
}
