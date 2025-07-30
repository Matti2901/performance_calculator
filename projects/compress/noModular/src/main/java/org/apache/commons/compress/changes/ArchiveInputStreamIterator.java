package org.apache.commons.compress.changes;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;

import java.io.IOException;
import java.io.InputStream;

public final class ArchiveInputStreamIterator<E extends ArchiveEntry> implements ArchiveEntryIterator<E> {

    private final ArchiveInputStream<E> inputStream;
    private E next;

    public ArchiveInputStreamIterator(final ArchiveInputStream<E> inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public boolean hasNext() throws IOException {
        return (next = inputStream.getNextEntry()) != null;
    }

    @Override
    public E next() {
        return next;
    }
}
