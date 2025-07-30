
package org.apache.commons.compress.archivers.examples.clients;

import org.apache.commons.compress.archivers.ArchiveEntry;

import java.io.IOException;

@FunctionalInterface
public interface ArchiveEntrySupplier<T extends ArchiveEntry> {
    T get() throws IOException;

}
