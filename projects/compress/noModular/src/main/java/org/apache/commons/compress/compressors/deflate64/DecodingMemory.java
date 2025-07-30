package org.apache.commons.compress.compressors.deflate64;

final class DecodingMemory {
    private final byte[] memory;
    private final int mask;
    private int wHead;
    private boolean wrappedAround;

    DecodingMemory() {
        this(16);
    }

    private DecodingMemory(final int bits) {
        memory = new byte[1 << bits];
        mask = memory.length - 1;
    }

    byte add(final byte b) {
        memory[wHead] = b;
        wHead = incCounter(wHead);
        return b;
    }

    void add(final byte[] b, final int off, final int len) {
        for (int i = off; i < off + len; i++) {
            add(b[i]);
        }
    }

    private int incCounter(final int counter) {
        final int newCounter = counter + 1 & mask;
        if (!wrappedAround && newCounter < counter) {
            wrappedAround = true;
        }
        return newCounter;
    }

    void recordToBuffer(final int distance, final int length, final byte[] buff) {
        if (distance > memory.length) {
            throw new IllegalStateException("Illegal distance parameter: " + distance);
        }
        final int start = wHead - distance & mask;
        if (!wrappedAround && start >= wHead) {
            throw new IllegalStateException("Attempt to read beyond memory: dist=" + distance);
        }
        for (int i = 0, pos = start; i < length; i++, pos = incCounter(pos)) {
            buff[i] = add(memory[pos]);
        }
    }
}
