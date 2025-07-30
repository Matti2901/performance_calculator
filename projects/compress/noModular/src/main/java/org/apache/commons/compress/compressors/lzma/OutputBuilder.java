package org.apache.commons.compress.compressors.lzma;

import org.apache.commons.io.build.AbstractStreamBuilder;
import org.tukaani.xz.LZMA2Options;

import java.io.IOException;

/**
 * Builds a new {@link LZMACompressorOutputStream}.
 *
 * <p>
 * For example:
 * </p>
 * <pre>{@code
 * LZMACompressorOutputStream s = LZMACompressorOutputStream.builder()
 *   .setPath(path)
 *   .setLzma2Options(new LZMA2Options(...))
 *   .get();
 * }
 * </pre>
 *
 * @see #get()
 * @see LZMA2Options
 * @since 1.28.0
 */
// @formatter:on
public class OutputBuilder extends AbstractStreamBuilder<LZMACompressorOutputStream, OutputBuilder> {

    LZMA2Options lzma2Options = new LZMA2Options();

    /**
     * Constructs a new builder of {@link LZMACompressorOutputStream}.
     */
    public OutputBuilder() {
        // empty
    }

    @Override
    public LZMACompressorOutputStream get() throws IOException {
        return new LZMACompressorOutputStream(this);
    }

    /**
     * Sets LZMA options.
     * <p>
     * Passing {@code null} resets to the default value {@link LZMA2Options#LZMA2Options()}.
     * </p>
     *
     * @param lzma2Options LZMA options.
     * @return this instance.
     */
    public OutputBuilder setLzma2Options(final LZMA2Options lzma2Options) {
        this.lzma2Options = lzma2Options != null ? lzma2Options : new LZMA2Options();
        return this;
    }

}
