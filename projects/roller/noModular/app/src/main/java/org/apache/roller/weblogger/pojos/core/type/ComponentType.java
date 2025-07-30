package org.apache.roller.weblogger.pojos.core.type;

public enum ComponentType {
    WEBLOG("Weblog"),
    PERMALINK("Permalink"),
    SEARCH("Search"),
    TAGSINDEX("Tag Index"),
    STYLESHEET("Stylesheet"),
    CUSTOM("Custom");

    private final String readableName;

    ComponentType(String readableName) {
        this.readableName = readableName;
    }

    public String getReadableName() {
        return readableName;
    }
}
