package org.apache.commons.compress.compressors.lz77support;

import java.util.Objects;

/**
 * Base class representing blocks the compressor may emit.
 *
 * <p>
 * This class is not supposed to be subclassed by classes outside of Commons Compress so it is considered internal and changed that would break subclasses
 * may get introduced with future releases.
 * </p>
 */
public abstract class Block {

    private final BlockType type;

    /**
     * Constructs a new typeless instance.
     *
     * @deprecated Use {@link #Block()}.
     */
    @Deprecated
    public Block() {
        this.type = null;
    }

    /**
     * Constructs a new instance.
     *
     * @param type the block type, may not be {@code null}.
     */
    protected Block(final BlockType type) {
        this.type = Objects.requireNonNull(type);
    }

    /**
     * Gets the the block type.
     *
     * @return the the block type.
     */
    public BlockType getType() {
        return type;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + getType();
    }
}
