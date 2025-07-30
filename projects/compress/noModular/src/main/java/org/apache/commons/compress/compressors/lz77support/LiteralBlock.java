package org.apache.commons.compress.compressors.lz77support;

/**
 * Represents a literal block of data.
 *
 * <p>
 * For performance reasons this encapsulates the real data, not a copy of it. Don't modify the data and process it inside of {@link Callback#accept}
 * immediately as it will get overwritten sooner or later.
 * </p>
 */
public final class LiteralBlock extends AbstractReference {

    private final byte[] data;

    /**
     * Constructs a new instance.
     *
     * @param data   the literal data.
     * @param offset the length of literal block.
     * @param length the length of literal block.
     */
    public LiteralBlock(final byte[] data, final int offset, final int length) {
        super(BlockType.LITERAL, offset, length);
        this.data = data;
    }

    /**
     * Gets the literal data.
     *
     * <p>
     * This returns a live view of the actual data in order to avoid copying, modify the array at your own risk.
     * </p>
     *
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

}
