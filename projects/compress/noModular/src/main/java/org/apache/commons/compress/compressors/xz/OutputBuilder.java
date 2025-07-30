package org.apache.commons.compress.compressors.xz;

import org.apache.commons.io.build.AbstractStreamBuilder;
import org.tukaani.xz.LZMA2Options;

import java.io.IOException;

/**
 * Builds a new {@link XZCompressorOutputStream}.
 *
 * <p>
 * For example:
 * </p>
 * <pre>{@code
 * XZCompressorOutputStream s = XZCompressorOutputStream.builder()
 *   .setPath(path)
 *   .setLzma2Options(new LZMA2Options(...))
 *   .get();
 * }
 * </pre>
 *
 * @see #get()
 * @since 1.28.0
 */
// @formatter:on
public class OutputBuilder extends AbstractStreamBuilder<XZCompressorOutputStream, OutputBuilder> {

    LZMA2Options lzma2Options = new LZMA2Options();

    /**
     * Constructs a new builder of {@link XZCompressorOutputStream}.
     */
    public OutputBuilder() {
        // empty
    }

    @Override
    public XZCompressorOutputStream get() throws IOException {
        return new XZCompressorOutputStream(this);
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
