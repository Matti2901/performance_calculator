package org.apache.commons.compress.compressors.lz77support;

/**
 * Represents a back-reference.
 */
public final class BackReference extends AbstractReference {

    /**
     * Constructs a new instance.
     *
     * @param offset the offset of the back-reference.
     * @param length the offset of the back-reference.
     */
    public BackReference(final int offset, final int length) {
        super(BlockType.BACK_REFERENCE, offset, length);
    }

}
