package org.apache.commons.compress.compressors.deflate64;

import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.compress.utils.ExactMath;

import java.io.IOException;

import static org.apache.commons.compress.compressors.deflate64.HuffmanState.INITIAL;

final class HuffmanCodes extends DecoderState {
    private final HuffmanDecoder huffmanDecoder;
    private boolean endOfBlock;
    private final HuffmanState state;
    private final BinaryTreeNode lengthTree;
    private final BinaryTreeNode distanceTree;

    private int runBufferPos;
    private byte[] runBuffer = ByteUtils.EMPTY_BYTE_ARRAY;
    private int runBufferLength;

    HuffmanCodes(HuffmanDecoder huffmanDecoder, final HuffmanState state, final int[] lengths, final int[] distance) {
        this.huffmanDecoder = huffmanDecoder;
        this.state = state;
        lengthTree = HuffmanDecoder.buildTree(lengths);
        distanceTree = HuffmanDecoder.buildTree(distance);
    }

    @Override
    int available() {
        return runBufferLength - runBufferPos;
    }

    private int copyFromRunBuffer(final byte[] b, final int off, final int len) {
        final int bytesInBuffer = runBufferLength - runBufferPos;
        int copiedBytes = 0;
        if (bytesInBuffer > 0) {
            copiedBytes = Math.min(len, bytesInBuffer);
            System.arraycopy(runBuffer, runBufferPos, b, off, copiedBytes);
            runBufferPos += copiedBytes;
        }
        return copiedBytes;
    }

    private int decodeNext(final byte[] b, final int off, final int len) throws IOException {
        if (endOfBlock) {
            return -1;
        }
        int result = copyFromRunBuffer(b, off, len);

        while (result < len) {
            final int symbol = HuffmanDecoder.nextSymbol(huffmanDecoder.reader, lengthTree);
            if (symbol < 256) {
                b[off + result++] = huffmanDecoder.memory.add((byte) symbol);
            } else if (symbol > 256) {
                final int runMask = HuffmanDecoder.RUN_LENGTH_TABLE[symbol - 257];
                int run = runMask >>> 5;
                final int runXtra = runMask & 0x1F;
                run = ExactMath.add(run, huffmanDecoder.readBits(runXtra));

                final int distSym = HuffmanDecoder.nextSymbol(huffmanDecoder.reader, distanceTree);

                final int distMask = HuffmanDecoder.DISTANCE_TABLE[distSym];
                int dist = distMask >>> 4;
                final int distXtra = distMask & 0xF;
                dist = ExactMath.add(dist, huffmanDecoder.readBits(distXtra));

                if (runBuffer.length < run) {
                    runBuffer = new byte[run];
                }
                runBufferLength = run;
                runBufferPos = 0;
                huffmanDecoder.memory.recordToBuffer(dist, run, runBuffer);

                result += copyFromRunBuffer(b, off + result, len - result);
            } else {
                endOfBlock = true;
                return result;
            }
        }

        return result;
    }

    @Override
    boolean hasData() {
        return !endOfBlock;
    }

    @Override
    int read(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        return decodeNext(b, off, len);
    }

    @Override
    HuffmanState state() {
        return endOfBlock ? INITIAL : state;
    }
}
