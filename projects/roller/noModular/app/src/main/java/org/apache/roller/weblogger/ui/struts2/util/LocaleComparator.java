package org.apache.roller.weblogger.ui.struts2.util;

import java.util.Comparator;
import java.util.Locale;

// special comparator for sorting LOCALES
public final class LocaleComparator implements Comparator<Locale> {
    @Override
    public int compare(Locale locale1, Locale locale2) {
        int compName = locale1.getDisplayName().compareTo(locale2.getDisplayName());
        if (compName == 0) {
            return locale1.toString().compareTo(locale2.toString());
        }
        return compName;
    }
}
