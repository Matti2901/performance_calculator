package org.apache.commons.compress.archivers.sevenz;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_16LE;

public class AES256Utils {
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
}
