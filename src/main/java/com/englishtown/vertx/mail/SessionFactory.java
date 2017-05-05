package com.englishtown.vertx.mail;

import javax.mail.Session;
import java.util.Properties;

/**
 * Factory to create {@link javax.mail.Session} instances
 */
public interface SessionFactory {

    Session getInstance(Properties props);

}
