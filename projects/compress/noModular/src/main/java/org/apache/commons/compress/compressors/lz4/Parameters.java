package org.apache.commons.compress.compressors.lz4;

/**
 * Parameters of the LZ4 frame format.
 */
public class Parameters {

    /**
     * The default parameters of 4M block size, enabled content checksum, disabled block checksums and independent blocks.
     *
     * <p>
     * This matches the defaults of the lz4 command line utility.
     * </p>
     */
    public static final Parameters DEFAULT = new Parameters(BlockSize.M4, true, false, false);
    final BlockSize blockSize;
    final boolean withContentChecksum;
    final boolean withBlockChecksum;
    final boolean withBlockDependency;

    final org.apache.commons.compress.compressors.lz77support.Parameters lz77params;

    /**
     * Sets up custom a custom block size for the LZ4 stream but otherwise uses the defaults of enabled content checksum, disabled block checksums and
     * independent blocks.
     *
     * @param blockSize the size of a single block.
     */
    public Parameters(final BlockSize blockSize) {
        this(blockSize, true, false, false);
    }

    /**
     * Sets up custom parameters for the LZ4 stream.
     *
     * @param blockSize           the size of a single block.
     * @param withContentChecksum whether to write a content checksum
     * @param withBlockChecksum   whether to write a block checksum. Note that block checksums are not supported by the lz4 command line utility
     * @param withBlockDependency whether a block may depend on the content of a previous block. Enabling this may improve compression ratio but makes it
     *                            impossible to decompress the output in parallel.
     */
    public Parameters(final BlockSize blockSize, final boolean withContentChecksum, final boolean withBlockChecksum, final boolean withBlockDependency) {
        this(blockSize, withContentChecksum, withBlockChecksum, withBlockDependency, BlockLZ4CompressorOutputStream.createParameterBuilder().build());
    }

    /**
     * Sets up custom parameters for the LZ4 stream.
     *
     * @param blockSize           the size of a single block.
     * @param withContentChecksum whether to write a content checksum
     * @param withBlockChecksum   whether to write a block checksum. Note that block checksums are not supported by the lz4 command line utility
     * @param withBlockDependency whether a block may depend on the content of a previous block. Enabling this may improve compression ratio but makes it
     *                            impossible to decompress the output in parallel.
     * @param lz77params          parameters used to fine-tune compression, in particular to balance compression ratio vs compression speed.
     */
    public Parameters(final BlockSize blockSize, final boolean withContentChecksum, final boolean withBlockChecksum, final boolean withBlockDependency,
                      final org.apache.commons.compress.compressors.lz77support.Parameters lz77params) {
        this.blockSize = blockSize;
        this.withContentChecksum = withContentChecksum;
        this.withBlockChecksum = withBlockChecksum;
        this.withBlockDependency = withBlockDependency;
        this.lz77params = lz77params;
    }

    /**
     * Sets up custom a custom block size for the LZ4 stream but otherwise uses the defaults of enabled content checksum, disabled block checksums and
     * independent blocks.
     *
     * @param blockSize  the size of a single block.
     * @param lz77params parameters used to fine-tune compression, in particular to balance compression ratio vs compression speed.
     */
    public Parameters(final BlockSize blockSize, final org.apache.commons.compress.compressors.lz77support.Parameters lz77params) {
        this(blockSize, true, false, false, lz77params);
    }

    @Override
    public String toString() {
        return "LZ4 Parameters with BlockSize " + blockSize + ", withContentChecksum " + withContentChecksum + ", withBlockChecksum " + withBlockChecksum
                + ", withBlockDependency " + withBlockDependency;
    }
}
