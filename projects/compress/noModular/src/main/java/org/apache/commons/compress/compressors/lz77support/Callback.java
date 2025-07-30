package org.apache.commons.compress.compressors.lz77support;

import java.io.IOException;

/**
 * Callback invoked while the compressor processes data.
 *
 * <p>
 * The callback is invoked on the same thread that receives the bytes to compress and may be invoked multiple times during the execution of
 * {@link #compress} or {@link #finish}.
 * </p>
 */
public interface Callback {

    /**
     * Consumes a block.
     *
     * @param b the block to consume
     * @throws IOException in case of an error
     */
    void accept(Block b) throws IOException;
}
