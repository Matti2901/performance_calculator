package org.apache.commons.compress.compressors.lz77support;

public interface IParams {
    int getLazyMatchingThreshold();

    boolean getLazyMatching();

    int getMinBackReferenceLength();

    int getMaxOffset();

    int getWindowSize();

    int getMaxLiteralLength();

    int getMaxBackReferenceLength();

    int getNiceBackReferenceLength();

    int getMaxCandidates();
}
