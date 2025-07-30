package org.apache.jackrabbit.core.query.lucene.directory.fs;

import org.apache.jackrabbit.core.query.lucene.IOCounters;
import org.apache.lucene.store.IndexInput;

import java.io.IOException;

/**
 * Implements an index input wrapper that logs the number of time bytes
 * are read from storage.
 */
public final class IndexInputLogWrapper extends IndexInput {

    private IndexInput in;

    IndexInputLogWrapper(String name, IndexInput in) {
        super(name);
        this.in = in;
    }

    @Override
    public byte readByte() throws IOException {
        return in.readByte();
    }

    @Override
    public void readBytes(byte[] b, int offset, int len) throws IOException {
        IOCounters.incrRead();
        in.readBytes(b, offset, len);
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public long getFilePointer() {
        return in.getFilePointer();
    }

    @Override
    public void seek(long pos) throws IOException {
        in.seek(pos);
    }

    @Override
    public long length() {
        return in.length();
    }

    @Override
    public Object clone() {
        IndexInputLogWrapper clone = (IndexInputLogWrapper) super.clone();
        clone.in = (IndexInput) in.clone();
        return clone;
    }
}
