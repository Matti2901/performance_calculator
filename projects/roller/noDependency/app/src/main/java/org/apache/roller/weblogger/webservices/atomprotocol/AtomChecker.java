package org.apache.roller.weblogger.webservices.atomprotocol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.util.RollerConstants;
import org.apache.roller.weblogger.config.WebloggerConfig;
import org.apache.roller.weblogger.pojos.User;
import org.apache.roller.weblogger.pojos.Weblog;
import org.apache.roller.weblogger.pojos.WeblogEntry;
import org.apache.roller.weblogger.pojos.WeblogPermission;

public class AtomChecker {
    /**
     * Return true if user is allowed to edit an entry.
     */

    protected static final boolean THROTTLE;
    protected static Log log = LogFactory.getFactory().getInstance(AtomChecker.class);
    static {
        THROTTLE = WebloggerConfig
                .getBooleanProperty("webservices.atomprotocol.oneSecondThrottle", true);
    }

    public static boolean canEdit(User u, WeblogEntry entry) {
        try {
            return entry.hasWritePermissions(u);
        } catch (Exception e) {
            log.error("Checking website.canSave()");
        }
        return false;
    }

    /**
     * Return true if user is allowed to view an entry.
     */
    public static boolean canView(User u, WeblogEntry entry) {
        return canEdit(u, entry);
    }

    /**
     * Return true if user is allowed to create/edit weblog entries and file uploads in a website.
     */
    public static  boolean canEdit(User u, Weblog website) {
        try {
            return website.hasUserPermission(u, WeblogPermission.POST);
        } catch (Exception e) {
            log.error("Checking website.hasUserPermissions()");
        }
        return false;
    }

    /**
     * Return true if user is allowed to view a website.
     */
    public static boolean canView(User u, Weblog website) {
        return canEdit(u, website);
    }

    public static void oneSecondThrottle() {
        // Throttle one entry per second per weblog because time-
        // stamp in MySQL and other DBs has only 1 sec resolution
        if (THROTTLE) {
            try {
                synchronized (AtomChecker.class) {
                    Thread.sleep(RollerConstants.SEC_IN_MS);
                }
            } catch (Exception ignored) {}
        }
    }
}
