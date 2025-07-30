package org.apache.commons.compress.archivers.sevenz.coder.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public final class DeflateDecoderOutputStream extends OutputStream {

    final DeflaterOutputStream deflaterOutputStream;
    public Deflater deflater;

    public DeflateDecoderOutputStream(final DeflaterOutputStream deflaterOutputStream, final Deflater deflater) {
        this.deflaterOutputStream = deflaterOutputStream;
        this.deflater = deflater;
    }

    @Override
    public void close() throws IOException {
        try {
            deflaterOutputStream.close();
        } finally {
            deflater.end();
        }
    }

    @Override
    public void write(final byte[] b) throws IOException {
        deflaterOutputStream.write(b);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        deflaterOutputStream.write(b, off, len);
    }

    @Override
    public void write(final int b) throws IOException {
        deflaterOutputStream.write(b);
    }
}
