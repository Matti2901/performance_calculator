package org.apache.roller.weblogger.util.i18manage;

import org.apache.roller.weblogger.WebloggerException;

/**
 * An exception thrown if there is a problem sending an email.
 */
public class MailingException extends WebloggerException {
    public MailingException(Throwable t) {
        super(t);
    }
}
