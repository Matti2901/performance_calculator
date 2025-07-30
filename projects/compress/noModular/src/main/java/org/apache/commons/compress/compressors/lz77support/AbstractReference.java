package org.apache.commons.compress.compressors.lz77support;

/**
 * Represents a back-reference.
 */
public abstract class AbstractReference extends Block {

    private final int offset;
    private final int length;

    /**
     * Constructs a new instance.
     *
     * @param blockType The block type.
     * @param offset    the offset of the reference.
     * @param length    the offset of the reference.
     */
    public AbstractReference(final BlockType blockType, final int offset, final int length) {
        super(blockType);
        this.offset = offset;
        this.length = length;
    }

    /**
     * Gets the offset of the reference.
     *
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * Gets the offset of the reference.
     *
     * @return the offset
     */
    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return super.toString() + " with offset " + offset + " and length " + length;
    }
}
