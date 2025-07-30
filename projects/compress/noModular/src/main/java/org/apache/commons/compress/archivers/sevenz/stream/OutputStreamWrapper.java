package org.apache.commons.compress.archivers.sevenz.stream;

import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class OutputStreamWrapper extends OutputStream {

    private static final int BUF_SIZE = 8192;
    private final SevenZOutputFile sevenZOutputFile;
    private final ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);

    public OutputStreamWrapper(SevenZOutputFile sevenZOutputFile) {
        this.sevenZOutputFile = sevenZOutputFile;
    }

    @Override
    public void close() throws IOException {
        // the file will be closed by the containing class's close method
    }

    @Override
    public void flush() throws IOException {
        // no reason to flush the channel
    }

    @Override
    public void write(final byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (len > BUF_SIZE) {
            sevenZOutputFile.channel.write(ByteBuffer.wrap(b, off, len));
        } else {
            buffer.clear();
            buffer.put(b, off, len).flip();
            sevenZOutputFile.channel.write(buffer);
        }
        sevenZOutputFile.compressedCrc32.update(b, off, len);
        sevenZOutputFile.fileBytesWritten += len;
    }

    @Override
    public void write(final int b) throws IOException {
        buffer.clear();
        buffer.put((byte) b).flip();
        sevenZOutputFile.channel.write(buffer);
        sevenZOutputFile.compressedCrc32.update(b);
        sevenZOutputFile.fileBytesWritten++;
    }
}
