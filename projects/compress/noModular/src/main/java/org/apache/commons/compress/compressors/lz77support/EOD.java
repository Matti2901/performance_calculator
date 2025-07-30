package org.apache.commons.compress.compressors.lz77support;

/**
 * A simple "we are done" marker.
 */
public final class EOD extends Block {

    /**
     * Singleton instance.
     */
    static final EOD INSTANCE = new EOD();

    /**
     * Constructs a new instance.
     */
    public EOD() {
        super(BlockType.EOD);
    }

}
