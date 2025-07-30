package org.apache.commons.compress.compressors.lz4;

/**
 * Enumerates the block sizes supported by the format.
 */
public enum BlockSize {

    /**
     * Block size of 64K.
     */
    K64(64 * 1024, 4),

    /**
     * Block size of 256K.
     */
    K256(256 * 1024, 5),

    /**
     * Block size of 1M.
     */
    M1(1024 * 1024, 6),

    /**
     * Block size of 4M.
     */
    M4(4096 * 1024, 7);

    private final int size;
    private final int index;

    BlockSize(final int size, final int index) {
        this.size = size;
        this.index = index;
    }

    int getIndex() {
        return index;
    }

    int getSize() {
        return size;
    }
}
