package org.apache.commons.compress.compressors.deflate64;

import java.io.IOException;

abstract class DecoderState {
    abstract int available() throws IOException;

    abstract boolean hasData();

    abstract int read(byte[] b, int off, int len) throws IOException;

    abstract HuffmanState state();
}
