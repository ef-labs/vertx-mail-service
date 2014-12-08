package com.englishtown.vertx;

import com.englishtown.vertx.mail.ContentType;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link com.englishtown.vertx.mail.ContentType}
 */
public class ContentTypeTest {

    @Test
    public void testFromString_text_plain() {
        String type = "text/plain";
        ContentType contentType = ContentType.fromString(type);
        assertEquals(ContentType.TEXT_PLAIN, contentType);
    }

    @Test
    public void testFromString_text_html() {
        String type = "text/html";
        ContentType contentType = ContentType.fromString(type);
        assertEquals(ContentType.TEXT_HTML, contentType);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromString_illegal() {
        String type = "something";
        ContentType contentType = ContentType.fromString(type);
    }
}
