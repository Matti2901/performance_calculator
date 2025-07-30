package org.apache.roller.weblogger.util.LRU;

public class DefaultEnvironment implements Environment {
    @Override
    public long getCurrentTimeInMillis() {
        return System.currentTimeMillis();
    }
}
