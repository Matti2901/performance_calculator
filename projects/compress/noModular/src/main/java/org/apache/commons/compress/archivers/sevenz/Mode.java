package org.apache.commons.compress.archivers.sevenz;

import org.apache.commons.compress.archivers.sevenz.method.SevenZMethodConfiguration;

import java.io.IOException;

/**
 * Enumerates modes.
 */
enum Mode {
    LIST("Analysing") {
        private String getContentMethods(final SevenZArchiveEntry entry) {
            final StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (final SevenZMethodConfiguration m : entry.getContentMethods()) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                sb.append(m.getMethod());
                if (m.getOptions() != null) {
                    sb.append("(").append(m.getOptions()).append(")");
                }
            }
            return sb.toString();
        }

        @Override
        public void takeAction(final SevenZFile archive, final SevenZArchiveEntry entry) {
            System.out.print(entry.getName());
            if (entry.isDirectory()) {
                System.out.print(" dir");
            } else {
                System.out.print(" " + entry.getCompressedSize() + "/" + entry.getSize());
            }
            if (entry.getHasLastModifiedDate()) {
                System.out.print(" " + entry.getLastModifiedDate());
            } else {
                System.out.print(" no last modified date");
            }
            if (!entry.isDirectory()) {
                System.out.println(" " + getContentMethods(entry));
            } else {
                System.out.println();
            }
        }
    };

    private final String message;

    Mode(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public abstract void takeAction(SevenZFile archive, SevenZArchiveEntry entry) throws IOException;
}
