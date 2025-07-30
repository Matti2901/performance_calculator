package org.apache.commons.compress.archivers.examples.clients;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class ArchiverFileVisitor<O extends ArchiveOutputStream<E>, E extends ArchiveEntry> extends SimpleFileVisitor<Path> {

    private final O outputStream;
    private final Path directory;
    private final LinkOption[] linkOptions;

    public ArchiverFileVisitor(final O target, final Path directory, final LinkOption... linkOptions) {
        this.outputStream = target;
        this.directory = directory;
        this.linkOptions = linkOptions == null ? IOUtils.EMPTY_LINK_OPTIONS : linkOptions.clone();
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
        return visit(dir, attrs, false);
    }

    protected FileVisitResult visit(final Path path, final BasicFileAttributes attrs, final boolean isFile) throws IOException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(attrs);
        final String name = directory.relativize(path).toString().replace('\\', '/');
        if (!name.isEmpty()) {
            final E archiveEntry = outputStream.createArchiveEntry(path, isFile || name.endsWith("/") ? name : name + "/", linkOptions);
            outputStream.putArchiveEntry(archiveEntry);
            if (isFile) {
                // Refactor this as a BiConsumer on Java 8?
                outputStream.write(path);
            }
            outputStream.closeArchiveEntry();
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        return visit(file, attrs, true);
    }
}
