package org.apache.roller.weblogger.util.cache;

import java.util.LinkedHashMap;
import java.util.Map;

// David Flanaghan: http://www.davidflanagan.com/blog/000014.html
public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    protected int maxsize;

    public LRULinkedHashMap(int maxsize) {
        super(maxsize * 4 / 3 + 1, 0.75f, true);
        this.maxsize = maxsize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > this.maxsize;
    }
}
