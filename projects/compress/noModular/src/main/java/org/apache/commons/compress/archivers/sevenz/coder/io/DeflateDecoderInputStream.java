package org.apache.commons.compress.archivers.sevenz.coder.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public final class DeflateDecoderInputStream extends FilterInputStream {

    public Inflater inflater;

    public DeflateDecoderInputStream(final InflaterInputStream inflaterInputStream, final Inflater inflater) {
        super(inflaterInputStream);
        this.inflater = inflater;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            inflater.end();
        }
    }

}
