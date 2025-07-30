package org.apache.commons.compress.archivers.sevenz;

import org.apache.commons.compress.exception.MemoryLimitException;

import java.io.IOException;
import java.util.BitSet;

final class ArchiveStatistics {
    public int numberOfPackedStreams;
    public long numberOfCoders;
    public long numberOfOutStreams;
    public long numberOfInStreams;
    public long numberOfUnpackSubStreams;
    public int numberOfFolders;
    public BitSet folderHasCrc;
    public int numberOfEntries;
    public int numberOfEntriesWithStream;

    /**
     * Asserts the validity of the given input.
     *
     * @param maxMemoryLimitKiB kibibytes (KiB) to test.
     * @throws IOException Thrown on basic assertion failure.
     */
    void assertValidity(final int maxMemoryLimitKiB) throws IOException {
        if (numberOfEntriesWithStream > 0 && numberOfFolders == 0) {
            throw new IOException("archive with entries but no folders");
        }
        if (numberOfEntriesWithStream > numberOfUnpackSubStreams) {
            throw new IOException("archive doesn't contain enough substreams for entries");
        }

        final long memoryNeededInKiB = estimateSize() / 1024;
        if (maxMemoryLimitKiB < memoryNeededInKiB) {
            throw new MemoryLimitException(memoryNeededInKiB, maxMemoryLimitKiB);
        }
    }

    private long bindPairSize() {
        return 16;
    }

    /**
     * Gets a size estimate in bytes.
     *
     * @return a size estimate in bytes.
     */
    private long coderSize() {
        return 2 /* methodId is between 1 and four bytes currently, COPY and LZMA2 are the most common with 1 */
                + 16 + 4 /* properties, guess */
                ;
    }

    /**
     * Gets a size estimate in bytes.
     *
     * @return a size estimate in bytes.
     */
    private long entrySize() {
        return 100; /* real size depends on name length, everything without name is about 70 bytes */
    }

    /**
     * Gets a size estimate in bytes.
     *
     * @return a size estimate in bytes.
     */
    long estimateSize() {
        final long lowerBound = 16L * numberOfPackedStreams /* packSizes, packCrcs in Archive */
                + numberOfPackedStreams / 8 /* packCrcsDefined in Archive */
                + numberOfFolders * folderSize() /* folders in Archive */
                + numberOfCoders * coderSize() /* coders in Folder */
                + (numberOfOutStreams - numberOfFolders) * bindPairSize() /* bindPairs in Folder */
                + 8L * (numberOfInStreams - numberOfOutStreams + numberOfFolders) /* packedStreams in Folder */
                + 8L * numberOfOutStreams /* unpackSizes in Folder */
                + numberOfEntries * entrySize() /* files in Archive */
                + streamMapSize();
        return 2 * lowerBound /* conservative guess */;
    }

    private long folderSize() {
        return 30; /* nested arrays are accounted for separately */
    }

    private long streamMapSize() {
        return 8 * numberOfFolders /* folderFirstPackStreamIndex, folderFirstFileIndex */
                + 8 * numberOfPackedStreams /* packStreamOffsets */
                + 4 * numberOfEntries /* fileFolderIndex */
                ;
    }

    @Override
    public String toString() {
        return String.format("Archive with %,d entries in %,d folders, estimated size %,d KiB.", numberOfEntries, numberOfFolders, estimateSize() / 1024L);
    }
}
