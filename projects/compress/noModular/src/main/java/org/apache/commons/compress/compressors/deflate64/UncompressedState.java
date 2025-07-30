package org.apache.commons.compress.compressors.deflate64;

import java.io.EOFException;
import java.io.IOException;

import static org.apache.commons.compress.compressors.deflate64.HuffmanState.INITIAL;
import static org.apache.commons.compress.compressors.deflate64.HuffmanState.STORED;

final class UncompressedState extends DecoderState {
    private final HuffmanDecoder huffmanDecoder;
    private final long blockLength;
    private long read;

    UncompressedState(HuffmanDecoder huffmanDecoder, final long blockLength) {
        this.huffmanDecoder = huffmanDecoder;
        this.blockLength = blockLength;
    }

    @Override
    int available() throws IOException {
        return (int) Math.min(blockLength - read, huffmanDecoder.reader.bitsAvailable() / Byte.SIZE);
    }

    @Override
    boolean hasData() {
        return read < blockLength;
    }

    @Override
    int read(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        // as len is an int and (blockLength - read) is >= 0 the min must fit into an int as well
        final int max = (int) Math.min(blockLength - read, len);
        int readSoFar = 0;
        while (readSoFar < max) {
            final int readNow;
            if (huffmanDecoder.reader.bitsCached() > 0) {
                final byte next = (byte) huffmanDecoder.readBits(Byte.SIZE);
                b[off + readSoFar] = huffmanDecoder.memory.add(next);
                readNow = 1;
            } else {
                readNow = huffmanDecoder.in.read(b, off + readSoFar, max - readSoFar);
                if (readNow == -1) {
                    throw new EOFException("Truncated Deflate64 Stream");
                }
                huffmanDecoder.memory.add(b, off + readSoFar, readNow);
            }
            read += readNow;
            readSoFar += readNow;
        }
        return max;
    }

    @Override
    HuffmanState state() {
        return read < blockLength ? STORED : INITIAL;
    }
}
