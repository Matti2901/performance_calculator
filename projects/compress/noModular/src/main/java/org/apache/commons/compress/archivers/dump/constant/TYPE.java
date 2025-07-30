package org.apache.commons.compress.archivers.dump.constant;

/**
 * Enumerates types.
 */
public enum TYPE {

    /**
     * WHITEOUT with code 14.
     */
    WHITEOUT(14),

    /**
     * SOCKET with code 12.
     */
    SOCKET(12),

    /**
     * LINK with code 10.
     */
    LINK(10),

    /**
     * FILE with code 8.
     */
    FILE(8),

    /**
     * BLKDEV with code 6.
     */
    BLKDEV(6),

    /**
     * DIRECTORY with code 4.
     */
    DIRECTORY(4),

    /**
     * CHRDEV with code 2.
     */
    CHRDEV(2),

    /**
     * CHRDEV with code 1.
     */
    FIFO(1),

    /**
     * UNKNOWN with code 15.
     */
    UNKNOWN(15);

    /**
     * Finds a matching enumeration value for the given code.
     *
     * @param code a code.
     * @return a value, never null.
     */
    public static TYPE find(final int code) {
        TYPE type = UNKNOWN;
        for (final TYPE t : values()) {
            if (code == t.code) {
                type = t;
            }
        }
        return type;
    }

    private final int code;

    TYPE(final int code) {
        this.code = code;
    }
}
