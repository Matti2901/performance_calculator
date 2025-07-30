package org.apache.roller.weblogger.util.LRU;

import java.util.LinkedHashMap;
import java.util.Map;

// David Flanaghan: http://www.davidflanagan.com/blog/000014.html
class LRULinkedHashMap extends LinkedHashMap<Object, CacheEntry> {
    protected int maxsize;

    public LRULinkedHashMap(int maxsize) {
        super(maxsize * 4 / 3 + 1, 0.75f, true);
        this.maxsize = maxsize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Object, CacheEntry> eldest) {
        return this.size() > this.maxsize;
    }
}
