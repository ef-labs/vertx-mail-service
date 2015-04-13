package com.englishtown.vertx.mail;

/**
 * Mail content type enum
 */
public enum ContentType {

    TEXT_PLAIN("text/plain", "plain"),
    TEXT_HTML("text/html", "html");

    private final String type;
    private final String subType;

    ContentType(String type, String subType) {
        this.type = type;
        this.subType = subType;
    }

    /**
     * Get the String representation of the ContentType
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Get the String representation of the subType
     *
     * @return
     */
    public String getSubType() {
        return subType;
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
