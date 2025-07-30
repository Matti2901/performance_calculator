package org.apache.commons.compress.compressors.lz77support;

/**
 * Enumerates the block types the compressor emits.
 */
public enum BlockType {

    /**
     * The literal block type.
     */
    LITERAL,

    /**
     * The back-reference block type.
     */
    BACK_REFERENCE,

    /**
     * The end-of-data block type.
     */
    EOD
}
