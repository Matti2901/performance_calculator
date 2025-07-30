
package org.apache.commons.compress.archivers.examples.clients;

import org.apache.commons.compress.archivers.ArchiveEntry;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface ArchiveEntryBiConsumer<T extends ArchiveEntry> {
    void accept(T entry, OutputStream out) throws IOException;
}
