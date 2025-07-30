package org.apache.commons.compress.changes;

import org.apache.commons.compress.archivers.ArchiveEntry;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstracts getting entries and streams for archive entries.
 *
 * <p>
 * Iterator#hasNext is not allowed to throw exceptions that's why we can't use Iterator&lt;ArchiveEntry&gt; directly - otherwise we'd need to convert
 * exceptions thrown in ArchiveInputStream#getNextEntry.
 * </p>
 */
public interface ArchiveEntryIterator<E extends ArchiveEntry> {

    InputStream getInputStream() throws IOException;

    boolean hasNext() throws IOException;

    E next();
}
