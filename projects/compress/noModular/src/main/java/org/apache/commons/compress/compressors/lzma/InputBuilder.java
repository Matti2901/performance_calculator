package org.apache.commons.compress.compressors.lzma;

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
 * LZMACompressorOutputStream s = LZMACompressorInputStream.builder()
 *   .setPath(path)
 *   .get();
 * }
 * </pre>
 *
 * @see #get()
 * @see LZMA2Options
 * @since 1.28.0
 */
// @formatter:on
public class InputBuilder extends AbstractStreamBuilder<LZMACompressorInputStream, InputBuilder> {

    int memoryLimitKiB = -1;

    @Override
    public LZMACompressorInputStream get() throws IOException {
        return new LZMACompressorInputStream(this);
    }

    /**
     * Sets a working memory threshold in kibibytes (KiB).
     *
     * @param memoryLimitKiB Sets a working memory threshold in kibibytes (KiB). Processing throws MemoryLimitException if memory use is above this
     *                       threshold.
     * @return this instance.
     */
    public InputBuilder setMemoryLimitKiB(int memoryLimitKiB) {
        this.memoryLimitKiB = memoryLimitKiB;
        return this;
    }
}
