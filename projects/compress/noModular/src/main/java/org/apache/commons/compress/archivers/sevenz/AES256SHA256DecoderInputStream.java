package org.apache.commons.compress.archivers.sevenz;

import org.apache.commons.compress.archivers.sevenz.coder.Coder;
import org.apache.commons.compress.exception.PasswordRequiredException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

final class AES256SHA256DecoderInputStream extends InputStream {
    private final InputStream in;
    private final Coder coder;
    private final String archiveName;
    private final byte[] passwordBytes;
    private boolean isInitialized;
    private CipherInputStream cipherInputStream;

    AES256SHA256DecoderInputStream(final InputStream in, final Coder coder, final String archiveName, final byte[] passwordBytes) {
        this.in = in;
        this.coder = coder;
        this.archiveName = archiveName;
        this.passwordBytes = passwordBytes;
    }

    @Override
    public void close() throws IOException {
        if (cipherInputStream != null) {
            cipherInputStream.close();
        }
    }

    private CipherInputStream init() throws IOException {
        if (isInitialized) {
            return cipherInputStream;
        }
        if (coder.properties == null) {
            throw new IOException("Missing AES256 properties in " + archiveName);
        }
        if (coder.properties.length < 2) {
            throw new IOException("AES256 properties too short in " + archiveName);
        }
        final int byte0 = 0xff & coder.properties[0];
        final int numCyclesPower = byte0 & 0x3f;
        final int byte1 = 0xff & coder.properties[1];
        final int ivSize = (byte0 >> 6 & 1) + (byte1 & 0x0f);
        final int saltSize = (byte0 >> 7 & 1) + (byte1 >> 4);
        if (2 + saltSize + ivSize > coder.properties.length) {
            throw new IOException("Salt size + IV size too long in " + archiveName);
        }
        final byte[] salt = new byte[saltSize];
        System.arraycopy(coder.properties, 2, salt, 0, saltSize);
        final byte[] iv = new byte[16];
        System.arraycopy(coder.properties, 2 + saltSize, iv, 0, ivSize);

        if (passwordBytes == null) {
            throw new PasswordRequiredException(archiveName);
        }
        final byte[] aesKeyBytes;
        if (numCyclesPower == 0x3f) {
            aesKeyBytes = new byte[32];
            System.arraycopy(salt, 0, aesKeyBytes, 0, saltSize);
            System.arraycopy(passwordBytes, 0, aesKeyBytes, saltSize, Math.min(passwordBytes.length, aesKeyBytes.length - saltSize));
        } else {
            aesKeyBytes = AES256SHA256Decoder.sha256Password(passwordBytes, numCyclesPower, salt);
        }

        final SecretKey aesKey = AES256Options.newSecretKeySpec(aesKeyBytes);
        try {
            final Cipher cipher = Cipher.getInstance(AES256Options.TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
            cipherInputStream = new CipherInputStream(in, cipher);
            isInitialized = true;
            return cipherInputStream;
        } catch (final GeneralSecurityException generalSecurityException) {
            throw new IllegalStateException("Decryption error (do you have the JCE Unlimited Strength Jurisdiction Policy Files installed?)",
                    generalSecurityException);
        }
    }

    @SuppressWarnings("resource") // Closed in close()
    @Override
    public int read() throws IOException {
        return init().read();
    }

    @SuppressWarnings("resource") // Closed in close()
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return init().read(b, off, len);
    }
}
