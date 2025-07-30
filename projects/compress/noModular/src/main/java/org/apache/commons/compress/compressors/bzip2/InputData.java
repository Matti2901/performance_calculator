package org.apache.commons.compress.compressors.bzip2;

final class InputData {

    // (with blockSize 900k)
    final boolean[] inUse = new boolean[256]; // 256 byte

    final byte[] seqToUnseq = new byte[256]; // 256 byte
    final byte[] selector = new byte[BZip2Constants.MAX_SELECTORS]; // 18002 byte
    final byte[] selectorMtf = new byte[BZip2Constants.MAX_SELECTORS]; // 18002 byte

    /**
     * Freq table collected to save a pass over the data during decompression.
     */
    final int[] unzftab = new int[256]; // 1024 byte

    final int[][] limit = new int[BZip2Constants.N_GROUPS][BZip2Constants.MAX_ALPHA_SIZE]; // 6192 byte
    final int[][] base = new int[BZip2Constants.N_GROUPS][BZip2Constants.MAX_ALPHA_SIZE]; // 6192 byte
    final int[][] perm = new int[BZip2Constants.N_GROUPS][BZip2Constants.MAX_ALPHA_SIZE]; // 6192 byte
    final int[] minLens = new int[BZip2Constants.N_GROUPS]; // 24 byte

    final int[] cftab = new int[257]; // 1028 byte
    final char[] getAndMoveToFrontDecode_yy = new char[256]; // 512 byte
    final char[][] temp_charArray2d = new char[BZip2Constants.N_GROUPS][BZip2Constants.MAX_ALPHA_SIZE]; // 3096
    // byte
    final byte[] recvDecodingTables_pos = new byte[BZip2Constants.N_GROUPS]; // 6 byte
    // ---------------
    // 60798 byte

    int[] tt; // 3600000 byte
    final byte[] ll8; // 900000 byte

    // ---------------
    // 4560782 byte
    // ===============

    InputData(final int blockSize100k) {
        this.ll8 = new byte[blockSize100k * BZip2Constants.BASEBLOCKSIZE];
    }

    /**
     * Initializes the {@link #tt} array.
     * <p>
     * This method is called when the required length of the array is known. I don't initialize it at construction time to avoid unnecessary memory
     * allocation when compressing small files.
     */
    int[] initTT(final int length) {
        int[] ttShadow = this.tt;

        // tt.length should always be >= length, but theoretically
        // it can happen, if the compressor mixed small and large
        // blocks. Normally only the last block will be smaller
        // than others.
        if (ttShadow == null || ttShadow.length < length) {
            this.tt = ttShadow = new int[length];
        }

        return ttShadow;
    }

}
