package org.apache.roller.weblogger.util.sanitaze;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the sanitizing results.
 * html is the sanitized html encoded  ready to be printed. Unaccepted tag are encode, text inside tag is always encoded    MUST BE USED WHEN PRINTING HTML
 * text is the text inside valid tags. Contains invalid tags encoded                                                        SHOULD BE USED TO PRINT EXCERPTS
 * val  is the html source cleaned from unaccepted tags. It is not encoded:                                                 SHOULD BE USED IN SAVE ACTIONS
 * isValid is true when every tag is accepted without forcing encoding
 * invalidTags is the list of encoded-killed tags
 */
class SanitizeResult {

    public String html = "";
    public String text = "";
    public String val = "";
    public boolean isValid = true;
    public List<String> invalidTags = new ArrayList<>();
}
