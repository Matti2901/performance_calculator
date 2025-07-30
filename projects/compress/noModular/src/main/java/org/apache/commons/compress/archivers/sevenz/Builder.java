package org.apache.commons.compress.archivers.sevenz;

import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.build.AbstractOrigin;
import org.apache.commons.io.build.AbstractStreamBuilder;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Builds new instances of {@link SevenZFile}.
 *
 * @since 1.26.0
 */
public class Builder extends AbstractStreamBuilder<SevenZFile, Builder> {

    static final int MEMORY_LIMIT_IN_KB = Integer.MAX_VALUE;
    static final boolean USE_DEFAULTNAME_FOR_UNNAMED_ENTRIES = false;
    static final boolean TRY_TO_RECOVER_BROKEN_ARCHIVES = false;

    private SeekableByteChannel seekableByteChannel;
    private String defaultName = SevenZFile.DEFAULT_FILE_NAME;
    private byte[] password;
    private int maxMemoryLimitKiB = MEMORY_LIMIT_IN_KB;
    private boolean useDefaultNameForUnnamedEntries = USE_DEFAULTNAME_FOR_UNNAMED_ENTRIES;
    private boolean tryToRecoverBrokenArchives = TRY_TO_RECOVER_BROKEN_ARCHIVES;

    @SuppressWarnings("resource") // Caller closes
    @Override
    public SevenZFile get() throws IOException {
        final SeekableByteChannel actualChannel;
        final String actualDescription;
        if (seekableByteChannel != null) {
            actualChannel = seekableByteChannel;
            actualDescription = defaultName;
        } else if (checkOrigin() instanceof AbstractOrigin.ByteArrayOrigin) {
            actualChannel = new SeekableInMemoryByteChannel(checkOrigin().getByteArray());
            actualDescription = defaultName;
        } else {
            OpenOption[] openOptions = getOpenOptions();
            if (ArrayUtils.isEmpty(openOptions)) {
                openOptions = new OpenOption[]{StandardOpenOption.READ};
            }
            final Path path = getPath();
            actualChannel = Files.newByteChannel(path, openOptions);
            actualDescription = path.toAbsolutePath().toString();
        }
        final boolean closeOnError = seekableByteChannel != null;
        return new SevenZFile(actualChannel, actualDescription, password, closeOnError, maxMemoryLimitKiB, useDefaultNameForUnnamedEntries,
                tryToRecoverBrokenArchives);
    }

    /**
     * Sets the default name.
     *
     * @param defaultName the default name.
     * @return {@code this} instance.
     */
    public Builder setDefaultName(final String defaultName) {
        this.defaultName = defaultName;
        return this;
    }

    /**
     * Sets the maximum amount of memory in kilobytes to use for parsing the archive and during extraction.
     * <p>
     * Not all codecs honor this setting. Currently only LZMA and LZMA2 are supported.
     * </p>
     *
     * @param maxMemoryLimitKiB the max memory limit in kilobytes.
     * @return {@code this} instance.
     */
    public Builder setMaxMemoryLimitKb(final int maxMemoryLimitKiB) {
        this.maxMemoryLimitKiB = maxMemoryLimitKiB / 1024;
        return this;
    }

    /**
     * Sets the maximum amount of memory in kilobytes to use for parsing the archive and during extraction.
     * <p>
     * Not all codecs honor this setting. Currently only LZMA and LZMA2 are supported.
     * </p>
     *
     * @param maxMemoryLimitKiB the max memory limit in kibibytes.
     * @return {@code this} instance.
     * @since 1.28.0
     */
    public Builder setMaxMemoryLimitKiB(final int maxMemoryLimitKiB) {
        this.maxMemoryLimitKiB = maxMemoryLimitKiB;
        return this;
    }

    /**
     * Sets the password.
     *
     * @param password the password.
     * @return {@code this} instance.
     */
    public Builder setPassword(final byte[] password) {
        this.password = password != null ? password.clone() : null;
        return this;
    }

    /**
     * Sets the password.
     *
     * @param password the password.
     * @return {@code this} instance.
     */
    public Builder setPassword(final char[] password) {
        this.password = password != null ? AES256SHA256Decoder.utf16Decode(password.clone()) : null;
        return this;
    }

    /**
     * Sets the password.
     *
     * @param password the password.
     * @return {@code this} instance.
     */
    public Builder setPassword(final String password) {
        this.password = password != null ? AES256SHA256Decoder.utf16Decode(password.toCharArray()) : null;
        return this;
    }

    /**
     * Sets the input channel.
     *
     * @param seekableByteChannel the input channel.
     * @return {@code this} instance.
     */
    public Builder setSeekableByteChannel(final SeekableByteChannel seekableByteChannel) {
        this.seekableByteChannel = seekableByteChannel;
        return this;
    }

    /**
     * Sets whether {@link SevenZFile} will try to recover broken archives where the CRC of the file's metadata is 0.
     * <p>
     * This special kind of broken archive is encountered when mutli volume archives are closed prematurely. If you enable this option SevenZFile will trust
     * data that looks as if it could contain metadata of an archive and allocate big amounts of memory. It is strongly recommended to not enable this
     * option without setting {@link #setMaxMemoryLimitKb(int)} at the same time.
     * </p>
     *
     * @param tryToRecoverBrokenArchives whether {@link SevenZFile} will try to recover broken archives where the CRC of the file's metadata is 0.
     * @return {@code this} instance.
     */
    public Builder setTryToRecoverBrokenArchives(final boolean tryToRecoverBrokenArchives) {
        this.tryToRecoverBrokenArchives = tryToRecoverBrokenArchives;
        return this;
    }

    /**
     * Sets whether entries without a name should get their names set to the archive's default file name.
     *
     * @param useDefaultNameForUnnamedEntries whether entries without a name should get their names set to the archive's default file name.
     * @return {@code this} instance.
     */
    public Builder setUseDefaultNameForUnnamedEntries(final boolean useDefaultNameForUnnamedEntries) {
        this.useDefaultNameForUnnamedEntries = useDefaultNameForUnnamedEntries;
        return this;
    }

}
