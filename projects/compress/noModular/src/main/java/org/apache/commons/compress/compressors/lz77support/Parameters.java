/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.compress.compressors.lz77support;

/**
 * Parameters of the {@link LZ77Compressor compressor}.
 */
public final class Parameters {

    /**
     * The hard-coded absolute minimal length of a back-reference.
     */
    public static final int TRUE_MIN_BACK_REFERENCE_LENGTH = LZ77Compressor.NUMBER_OF_BYTES_IN_HASH;

    /**
     * Initializes the builder for the compressor's parameters with a {@code minBackReferenceLength} of 3 and {@code max*Length} equal to
     * {@code windowSize - 1}.
     * <p>
     * It is recommended to not use this method directly but rather tune a pre-configured builder created by a format specific factory like
     * {@link org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream#createParameterBuilder}.
     * </p>
     *
     * @param windowSize the size of the sliding window - this determines the maximum offset a back-reference can take. Must be a power of two.
     * @throws IllegalArgumentException if windowSize is not a power of two.
     * @return a builder configured for the given window size
     */
    public static Builder builder(final int windowSize) {
        return new Builder(windowSize);
    }

    static boolean isPowerOfTwo(final int x) {
        // pre-condition: x > 0
        return (x & x - 1) == 0;
    }

    private final int windowSize;
    private final int minBackReferenceLength;
    private final int maxBackReferenceLength;
    private final int maxOffset;
    private final int maxLiteralLength;
    private final int niceBackReferenceLength;
    private final int maxCandidates;
    private final int lazyThreshold;

    private final boolean lazyMatching;

    public Parameters(final int windowSize, final int minBackReferenceLength, final int maxBackReferenceLength, final int maxOffset,
                      final int maxLiteralLength, final int niceBackReferenceLength, final int maxCandidates, final boolean lazyMatching, final int lazyThreshold) {
        this.windowSize = windowSize;
        this.minBackReferenceLength = minBackReferenceLength;
        this.maxBackReferenceLength = maxBackReferenceLength;
        this.maxOffset = maxOffset;
        this.maxLiteralLength = maxLiteralLength;
        this.niceBackReferenceLength = niceBackReferenceLength;
        this.maxCandidates = maxCandidates;
        this.lazyMatching = lazyMatching;
        this.lazyThreshold = lazyThreshold;
    }

    /**
     * Gets whether to perform lazy matching.
     *
     * @return whether to perform lazy matching
     */
    public boolean getLazyMatching() {
        return lazyMatching;
    }

    /**
     * Gets the threshold for lazy matching.
     *
     * @return the threshold for lazy matching
     */
    public int getLazyMatchingThreshold() {
        return lazyThreshold;
    }

    /**
     * Gets the maximal length of a back-reference found.
     *
     * @return the maximal length of a back-reference found
     */
    public int getMaxBackReferenceLength() {
        return maxBackReferenceLength;
    }

    /**
     * Gets the maximum number of back-reference candidates to consider.
     *
     * @return the maximum number of back-reference candidates to consider
     */
    public int getMaxCandidates() {
        return maxCandidates;
    }

    /**
     * Gets the maximal length of a literal block.
     *
     * @return the maximal length of a literal block
     */
    public int getMaxLiteralLength() {
        return maxLiteralLength;
    }

    /**
     * Gets the maximal offset of a back-reference found.
     *
     * @return the maximal offset of a back-reference found
     */
    public int getMaxOffset() {
        return maxOffset;
    }

    /**
     * Gets the minimal length of a back-reference found.
     *
     * @return the minimal length of a back-reference found
     */
    public int getMinBackReferenceLength() {
        return minBackReferenceLength;
    }

    /**
     * Gets the length of a back-reference that is considered nice enough to stop searching for longer ones.
     *
     * @return the length of a back-reference that is considered nice enough to stop searching
     */
    public int getNiceBackReferenceLength() {
        return niceBackReferenceLength;
    }

    /**
     * Gets the size of the sliding window - this determines the maximum offset a back-reference can take.
     *
     * @return the size of the sliding window
     */
    public int getWindowSize() {
        return windowSize;
    }
}
