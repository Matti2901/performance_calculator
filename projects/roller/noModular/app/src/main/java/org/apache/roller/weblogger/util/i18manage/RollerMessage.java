package org.apache.roller.weblogger.util.i18manage;

public class RollerMessage {
    private String mKey;
    private String[] mArgs;

    public RollerMessage(String key, String[] args) {
        mKey = key;
        mArgs = args;
    }

    public String[] getArgs() {
        return mArgs;
    }

    public void setArgs(String[] args) {
        mArgs = args;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }
}
