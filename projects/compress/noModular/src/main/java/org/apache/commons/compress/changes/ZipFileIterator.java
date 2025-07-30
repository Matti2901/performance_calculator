package org.apache.commons.compress.changes;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

public final class ZipFileIterator implements ArchiveEntryIterator<ZipArchiveEntry> {

    private final ZipFile zipFile;
    private final Enumeration<ZipArchiveEntry> nestedEnumeration;
    private ZipArchiveEntry currentEntry;

    public ZipFileIterator(final ZipFile zipFile) {
        this.zipFile = zipFile;
        this.nestedEnumeration = zipFile.getEntriesInPhysicalOrder();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return zipFile.getInputStream(currentEntry);
    }

    @Override
    public boolean hasNext() {
        return nestedEnumeration.hasMoreElements();
    }

    @Override
    public ZipArchiveEntry next() {
        return currentEntry = nestedEnumeration.nextElement();
    }
}
