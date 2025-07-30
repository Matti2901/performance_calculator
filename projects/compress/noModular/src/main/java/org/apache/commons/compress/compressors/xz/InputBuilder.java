package org.apache.commons.compress.compressors.xz;

import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.exception.MemoryLimitException;
import org.apache.commons.io.build.AbstractStreamBuilder;
import org.tukaani.xz.LZMA2Options;

import java.io.IOException;

/**
 * Builds a new {@link LZMACompressorInputStream}.
 *
 * <p>
 * For example:
 * </p>
 * <pre>{@code
 * XZCompressorInputStream s = XZCompressorInputStream.builder()
 *   .setPath(path)
 *   .setDecompressConcatenated(false)
 *   .setMemoryLimitKiB(-1)
 *   .get();
 * }
 * </pre>
 *
 * @see #get()
 * @see LZMA2Options
 * @since 1.28.0
 */
// @formatter:on
public class InputBuilder extends AbstractStreamBuilder<XZCompressorInputStream, InputBuilder> {

    int memoryLimitKiB = -1;
    boolean decompressConcatenated;

    @Override
    public XZCompressorInputStream get() throws IOException {
        return new XZCompressorInputStream(this);
    }

    /**
     * Whether to decompress until the end of the input.
     *
     * @param decompressConcatenated if true, decompress until the end of the input; if false, stop after the first .xz stream and leave the input position
     *                               to point to the next byte after the .xz stream
     * @return this instance.
     */
    public InputBuilder setDecompressConcatenated(final boolean decompressConcatenated) {
        this.decompressConcatenated = decompressConcatenated;
        return this;
    }


    /**
     * Sets a working memory threshold in kibibytes (KiB).
     *
     * @param memoryLimitKiB The memory limit used when reading blocks. The memory usage limit is expressed in kibibytes (KiB) or {@code -1} to impose no
     *                       memory usage limit. If the estimated memory limit is exceeded on {@link #read()}, a {@link MemoryLimitException} is thrown.
     * @return this instance.
     */
    public InputBuilder setMemoryLimitKiB(final int memoryLimitKiB) {
        this.memoryLimitKiB = memoryLimitKiB;
        return this;
    }
}
