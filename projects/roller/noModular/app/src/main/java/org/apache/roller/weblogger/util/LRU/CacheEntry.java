package org.apache.roller.weblogger.util.LRU;

class CacheEntry {
    private Object value;
    private long timeCached = -1;

    public CacheEntry(Object value, long timeCached) {
        this.timeCached = timeCached;
        this.value = value;
    }

    public long getTimeCached() {
        return timeCached;
    }

    public Object getValue() {
        return value;
    }
}
