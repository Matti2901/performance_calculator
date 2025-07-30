package org.apache.commons.compress.compressors.deflate64;

import java.io.IOException;

import static org.apache.commons.compress.compressors.deflate64.HuffmanState.INITIAL;

final class InitialState extends DecoderState {
    @Override
    int available() {
        return 0;
    }

    @Override
    boolean hasData() {
        return false;
    }

    @Override
    int read(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        throw new IllegalStateException("Cannot read in this state");
    }

    @Override
    HuffmanState state() {
        return INITIAL;
    }
}
