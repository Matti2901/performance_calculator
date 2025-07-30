package org.apache.commons.compress.compressors.bzip2;

final class Data {

    // with blockSize 900k
    /* maps unsigned byte => "does it occur in block" */
    final boolean[] inUse = new boolean[256]; // 256 byte
    final byte[] unseqToSeq = new byte[256]; // 256 byte
    final int[] mtfFreq = new int[BZip2Constants.MAX_ALPHA_SIZE]; // 1032 byte
    final byte[] selector = new byte[BZip2Constants.MAX_SELECTORS]; // 18002 byte
    final byte[] selectorMtf = new byte[BZip2Constants.MAX_SELECTORS]; // 18002 byte

    final byte[] generateMTFValues_yy = new byte[256]; // 256 byte
    final byte[][] sendMTFValues_len = new byte[BZip2Constants.N_GROUPS][BZip2Constants.MAX_ALPHA_SIZE]; // 1548
    // byte
    final int[][] sendMTFValues_rfreq = new int[BZip2Constants.N_GROUPS][BZip2Constants.MAX_ALPHA_SIZE]; // 6192
    // byte
    final int[] sendMTFValues_fave = new int[BZip2Constants.N_GROUPS]; // 24 byte
    final short[] sendMTFValues_cost = new short[BZip2Constants.N_GROUPS]; // 12 byte
    final int[][] sendMTFValues_code = new int[BZip2Constants.N_GROUPS][BZip2Constants.MAX_ALPHA_SIZE]; // 6192
    // byte
    final byte[] sendMTFValues2_pos = new byte[BZip2Constants.N_GROUPS]; // 6 byte
    final boolean[] sentMTFValues4_inUse16 = new boolean[16]; // 16 byte

    final int[] heap = new int[BZip2Constants.MAX_ALPHA_SIZE + 2]; // 1040 byte
    final int[] weight = new int[BZip2Constants.MAX_ALPHA_SIZE * 2]; // 2064 byte
    final int[] parent = new int[BZip2Constants.MAX_ALPHA_SIZE * 2]; // 2064 byte

    // ------------
    // 333408 byte

    /*
     * holds the RLEd block of original data starting at index 1. After sorting the last byte added to the buffer is at index 0.
     */
    final byte[] block; // 900021 byte
    /*
     * maps index in Burrows-Wheeler transformed block => index of byte in original block
     */
    final int[] fmap; // 3600000 byte
    final char[] sfmap; // 3600000 byte
    // ------------
    // 8433529 byte
    // ============

    /**
     * Index of original line in Burrows-Wheeler table.
     *
     * <p>
     * This is the index in fmap that points to the last byte of the original data.
     * </p>
     */
    int origPtr;

    Data(final int blockSize100k) {
        final int n = blockSize100k * BZip2Constants.BASEBLOCKSIZE;
        this.block = new byte[n + 1 + BZip2Constants.NUM_OVERSHOOT_BYTES];
        this.fmap = new int[n];
        this.sfmap = new char[2 * n];
    }

}
