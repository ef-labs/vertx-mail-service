package com.englishtown.vertx.mail;

/**
 * Mail content type enum
 */
public enum ContentType {

    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html");

    private final String type;

    private ContentType(String type) {
        this.type = type;
    }

    /**
     * Get the String representative of the ContentType
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Create a ContentType enum from a string
     *
     * @param type
     * @return ContentType enum
     * @throws IllegalArgumentException
     */
    public static ContentType fromString(String type) throws IllegalArgumentException {
        if ("text/plain".equals(type)) {
            return TEXT_PLAIN;
        }
        if ("text/html".equals(type)) {
            return TEXT_HTML;
        } else {
            throw new IllegalArgumentException("No ContentType defined for [" + type + "]");
        }
    }
}
