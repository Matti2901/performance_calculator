package org.apache.jackrabbit.core.persistence.db.blob;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SizedInputStream extends FilterInputStream {
    private final long size;
    private boolean consumed = false;

    public SizedInputStream(InputStream in, long size) {
        super(in);
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public int read() throws IOException {
        consumed = true;
        return super.read();
    }

    public long skip(long n) throws IOException {
        consumed = true;
        return super.skip(n);
    }

    public int read(byte[] b) throws IOException {
        consumed = true;
        return super.read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        consumed = true;
        return super.read(b, off, len);
    }
}
