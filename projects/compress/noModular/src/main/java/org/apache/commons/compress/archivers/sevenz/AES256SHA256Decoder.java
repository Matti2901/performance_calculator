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
package org.apache.commons.compress.archivers.sevenz;

import org.apache.commons.compress.archivers.sevenz.coder.AbstractCoder;
import org.apache.commons.compress.archivers.sevenz.coder.Coder;

import static java.nio.charset.StandardCharsets.UTF_16LE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.CipherOutputStream;

public final class AES256SHA256Decoder extends AbstractCoder {

    private static final class AES256SHA256DecoderOutputStream extends OutputStream {
        private final CipherOutputStream cipherOutputStream;
        // Ensures that data are encrypt in respect of cipher block size and pad with '0' if smaller
        // NOTE: As "AES/CBC/PKCS5Padding" is weak and should not be used, we use "AES/CBC/NoPadding" with this
        // manual implementation for padding possible thanks to the size of the file stored separately
        private final int cipherBlockSize;
        private final byte[] cipherBlockBuffer;
        private int count;

        private AES256SHA256DecoderOutputStream(final AES256Options opts, final OutputStream out) {
            cipherOutputStream = new CipherOutputStream(out, opts.getCipher());
            cipherBlockSize = opts.getCipher().getBlockSize();
            cipherBlockBuffer = new byte[cipherBlockSize];
        }

        @Override
        public void close() throws IOException {
            if (count > 0) {
                cipherOutputStream.write(cipherBlockBuffer);
            }
            cipherOutputStream.close();
        }

        @Override
        public void flush() throws IOException {
            cipherOutputStream.flush();
        }

        private void flushBuffer() throws IOException {
            cipherOutputStream.write(cipherBlockBuffer);
            count = 0;
            Arrays.fill(cipherBlockBuffer, (byte) 0);
        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            int gap = len + count > cipherBlockSize ? cipherBlockSize - count : len;
            System.arraycopy(b, off, cipherBlockBuffer, count, gap);
            count += gap;

            if (count == cipherBlockSize) {
                flushBuffer();

                if (len - gap >= cipherBlockSize) {
                    // skip buffer to encrypt data chunks big enough to fit cipher block size
                    final int multipleCipherBlockSizeLen = (len - gap) / cipherBlockSize * cipherBlockSize;
                    cipherOutputStream.write(b, off + gap, multipleCipherBlockSizeLen);
                    gap += multipleCipherBlockSizeLen;
                }
                System.arraycopy(b, off + gap, cipherBlockBuffer, 0, len - gap);
                count = len - gap;
            }
        }

        @Override
        public void write(final int b) throws IOException {
            cipherBlockBuffer[count++] = (byte) b;
            if (count == cipherBlockSize) {
                flushBuffer();
            }
        }
    }

    static byte[] sha256Password(final byte[] password, final int numCyclesPower, final byte[] salt) {
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (final NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new IllegalStateException("SHA-256 is unsupported by your Java implementation", noSuchAlgorithmException);
        }
        final byte[] extra = new byte[8];
        for (long j = 0; j < 1L << numCyclesPower; j++) {
            digest.update(salt);
            digest.update(password);
            digest.update(extra);
            for (int k = 0; k < extra.length; k++) {
                ++extra[k];
                if (extra[k] != 0) {
                    break;
                }
            }
        }
        return digest.digest();
    }

    static byte[] sha256Password(final char[] password, final int numCyclesPower, final byte[] salt) {
        return sha256Password(utf16Decode(password), numCyclesPower, salt);
    }

    /**
     * Convenience method that encodes Unicode characters into bytes in UTF-16 (little-endian byte order) charset
     *
     * @param chars characters to encode
     * @return encoded characters
     * @since 1.23
     */
    static byte[] utf16Decode(final char[] chars) {
        if (chars == null) {
            return null;
        }
        final ByteBuffer encoded = UTF_16LE.encode(CharBuffer.wrap(chars));
        if (encoded.hasArray()) {
            return encoded.array();
        }
        final byte[] e = new byte[encoded.remaining()];
        encoded.get(e);
        return e;
    }

    public AES256SHA256Decoder() {
        super(AES256Options.class);
    }

    @Override
    public InputStream decode(final String archiveName, final InputStream in, final long uncompressedLength, final Coder coder, final byte[] passwordBytes,
                       final int maxMemoryLimitKiB) {
        return new AES256SHA256DecoderInputStream(in, coder, archiveName, passwordBytes);
    }

    @Override
    public OutputStream encode(final OutputStream out, final Object options) throws IOException {
        return new AES256SHA256DecoderOutputStream((AES256Options) options, out);
    }

    @Override
    public byte[] getOptionsAsProperties(final Object options) throws IOException {
        final AES256Options opts = (AES256Options) options;
        final byte[] props = new byte[2 + opts.getSalt().length + opts.getIv().length];

        // First byte : control (numCyclesPower + flags of salt or iv presence)
        props[0] = (byte) (opts.getNumCyclesPower() | (opts.getSalt().length == 0 ? 0 : 1 << 7) | (opts.getIv().length == 0 ? 0 : 1 << 6));

        if (opts.getSalt().length != 0 || opts.getIv().length != 0) {
            // second byte : size of salt/iv data
            props[1] = (byte) ((opts.getSalt().length == 0 ? 0 : opts.getSalt().length - 1) << 4 | (opts.getIv().length == 0 ? 0 : opts.getIv().length - 1));

            // remain bytes : salt/iv data
            System.arraycopy(opts.getSalt(), 0, props, 2, opts.getSalt().length);
            System.arraycopy(opts.getIv(), 0, props, 2 + opts.getSalt().length, opts.getIv().length);
        }

        return props;
    }
}
