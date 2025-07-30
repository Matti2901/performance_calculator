package org.apache.roller.weblogger.pojos.core.template;

public enum TemplateLanguage {
    VELOCITY("Velocity");

    private final String readableName;

    TemplateLanguage(String readableName) {
        this.readableName = readableName;
    }

    public String getReadableName() {
        return readableName;
    }
}
