package org.apache.commons.compress.compressors.lz77support;

/**
 * Builder for {@link Parameters} instances.
 */
public class Builder {

    private final int windowSize;
    private int minBackReferenceLength;
    private int maxBackReferenceLength;
    private int maxOffset;
    private int maxLiteralLength;
    private Integer niceBackReferenceLength;
    private Integer maxCandidates;
    private Integer lazyThreshold;
    private Boolean lazyMatches;

    Builder(final int windowSize) {
        if (windowSize < 2 || !Parameters.isPowerOfTwo(windowSize)) {
            throw new IllegalArgumentException("windowSize must be a power of two");
        }
        this.windowSize = windowSize;
        minBackReferenceLength = Parameters.TRUE_MIN_BACK_REFERENCE_LENGTH;
        maxBackReferenceLength = windowSize - 1;
        maxOffset = windowSize - 1;
        maxLiteralLength = windowSize;
    }

    /**
     * Creates the {@link Parameters} instance.
     *
     * @return the configured {@link Parameters} instance.
     */
    public Parameters build() {
        // default settings tuned for a compromise of good compression and acceptable speed
        final int niceLen = niceBackReferenceLength != null ? niceBackReferenceLength : Math.max(minBackReferenceLength, maxBackReferenceLength / 2);
        final int candidates = maxCandidates != null ? maxCandidates : Math.max(256, windowSize / 128);
        final boolean lazy = lazyMatches == null || lazyMatches;
        final int threshold = lazy ? lazyThreshold != null ? lazyThreshold : niceLen : minBackReferenceLength;

        return new Parameters(windowSize, minBackReferenceLength, maxBackReferenceLength, maxOffset, maxLiteralLength, niceLen, candidates, lazy,
                threshold);
    }

    /**
     * Changes the default setting for "nice back-reference length" and "maximum number of candidates" for improved compression ratio at the cost of
     * compression speed.
     * <p>
     * Use this method after configuring "maximum back-reference length".
     * </p>
     *
     * @return the builder
     */
    public Builder tunedForCompressionRatio() {
        niceBackReferenceLength = lazyThreshold = maxBackReferenceLength;
        maxCandidates = Math.max(32, windowSize / 16);
        lazyMatches = true;
        return this;
    }

    /**
     * Changes the default setting for "nice back-reference length" and "maximum number of candidates" for improved compression speed at the cost of
     * compression ratio.
     * <p>
     * Use this method after configuring "maximum back-reference length".
     * </p>
     *
     * @return the builder
     */
    public Builder tunedForSpeed() {
        niceBackReferenceLength = Math.max(minBackReferenceLength, maxBackReferenceLength / 8);
        maxCandidates = Math.max(32, windowSize / 1024);
        lazyMatches = false;
        lazyThreshold = minBackReferenceLength;
        return this;
    }

    /**
     * Sets whether lazy matching should be performed.
     * <p>
     * Lazy matching means that after a back-reference for a certain position has been found the compressor will try to find a longer match for the next
     * position.
     * </p>
     * <p>
     * Lazy matching is enabled by default and disabled when tuning for speed.
     * </p>
     *
     * @param lazy whether lazy matching should be performed
     * @return the builder
     */
    public Builder withLazyMatching(final boolean lazy) {
        lazyMatches = lazy;
        return this;
    }

    /**
     * Sets the threshold for lazy matching.
     * <p>
     * Even if lazy matching is enabled it will not be performed if the length of the back-reference found for the current position is longer than this
     * value.
     * </p>
     *
     * @param threshold the threshold for lazy matching
     * @return the builder
     */
    public Builder withLazyThreshold(final int threshold) {
        lazyThreshold = threshold;
        return this;
    }

    /**
     * Sets the maximal length of a back-reference.
     * <p>
     * It is recommended to not use this method directly but rather tune a pre-configured builder created by a format specific factory like
     * {@link org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream#createParameterBuilder}.
     * </p>
     *
     * @param maxBackReferenceLength maximal length of a back-reference found. A value smaller than {@code minBackReferenceLength} is interpreted as
     *                               {@code minBackReferenceLength}. {@code maxBackReferenceLength} is capped at {@code windowSize - 1}.
     * @return the builder
     */
    public Builder withMaxBackReferenceLength(final int maxBackReferenceLength) {
        this.maxBackReferenceLength = maxBackReferenceLength < minBackReferenceLength ? minBackReferenceLength
                : Math.min(maxBackReferenceLength, windowSize - 1);
        return this;
    }

    /**
     * Sets the maximal length of a literal block.
     * <p>
     * It is recommended to not use this method directly but rather tune a pre-configured builder created by a format specific factory like
     * {@link org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream#createParameterBuilder}.
     * </p>
     *
     * @param maxLiteralLength maximal length of a literal block. Negative numbers and 0 as well as values bigger than {@code windowSize} are interpreted as
     *                         {@code windowSize}.
     * @return the builder
     */
    public Builder withMaxLiteralLength(final int maxLiteralLength) {
        this.maxLiteralLength = maxLiteralLength < 1 ? windowSize : Math.min(maxLiteralLength, windowSize);
        return this;
    }

    /**
     * Sets the maximum number of back-reference candidates that should be consulted.
     * <p>
     * This settings can be used to tune the tradeoff between compression speed and compression ratio.
     * </p>
     *
     * @param maxCandidates maximum number of back-reference candidates
     * @return the builder
     */
    public Builder withMaxNumberOfCandidates(final int maxCandidates) {
        this.maxCandidates = maxCandidates;
        return this;
    }

    /**
     * Sets the maximal offset of a back-reference.
     * <p>
     * It is recommended to not use this method directly but rather tune a pre-configured builder created by a format specific factory like
     * {@link org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream#createParameterBuilder}.
     * </p>
     *
     * @param maxOffset maximal offset of a back-reference. A non-positive value as well as values bigger than {@code windowSize - 1} are interpreted as
     *                  {@code windowSize * - 1}.
     * @return the builder
     */
    public Builder withMaxOffset(final int maxOffset) {
        this.maxOffset = maxOffset < 1 ? windowSize - 1 : Math.min(maxOffset, windowSize - 1);
        return this;
    }

    /**
     * Sets the minimal length of a back-reference.
     * <p>
     * Ensures {@code maxBackReferenceLength} is not smaller than {@code minBackReferenceLength}.
     * </p>
     * <p>
     * It is recommended to not use this method directly but rather tune a pre-configured builder created by a format specific factory like
     * {@link org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream#createParameterBuilder}.
     * </p>
     *
     * @param minBackReferenceLength the minimal length of a back-reference found. A true minimum of 3 is hard-coded inside of this implementation but
     *                               bigger lengths can be configured.
     * @return the builder
     * @throws IllegalArgumentException if {@code windowSize} is smaller than {@code minBackReferenceLength}.
     */
    public Builder withMinBackReferenceLength(final int minBackReferenceLength) {
        this.minBackReferenceLength = Math.max(Parameters.TRUE_MIN_BACK_REFERENCE_LENGTH, minBackReferenceLength);
        if (windowSize < this.minBackReferenceLength) {
            throw new IllegalArgumentException("minBackReferenceLength can't be bigger than windowSize");
        }
        if (maxBackReferenceLength < this.minBackReferenceLength) {
            maxBackReferenceLength = this.minBackReferenceLength;
        }
        return this;
    }

    /**
     * Sets the "nice length" of a back-reference.
     * <p>
     * When a back-references if this size has been found, stop searching for longer back-references.
     * </p>
     * <p>
     * This settings can be used to tune the tradeoff between compression speed and compression ratio.
     * </p>
     *
     * @param niceLen the "nice length" of a back-reference
     * @return the builder
     */
    public Builder withNiceBackReferenceLength(final int niceLen) {
        niceBackReferenceLength = niceLen;
        return this;
    }
}
