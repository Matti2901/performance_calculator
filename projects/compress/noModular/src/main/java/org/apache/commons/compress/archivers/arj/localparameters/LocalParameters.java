package org.apache.commons.compress.archivers.arj.localparameters;

public class LocalParameters {
    public static final class FileTypes {
        static final int BINARY = 0;
        static final int SEVEN_BIT_TEXT = 1;
        static final int COMMENT_HEADER = 2;
        public static final int DIRECTORY = 3;
        static final int VOLUME_LABEL = 4;
        static final int CHAPTER_LABEL = 5;
    }

    public static final class Flags {
        public static final int GARBLED = 0x01;
        public  static final int VOLUME = 0x04;
        public static final int EXTFILE = 0x08;
        public static final int PATHSYM = 0x10;
        public static final int BACKUP = 0x20;
    }

    public static final class Methods {
        public static final int STORED = 0;
        public static final int COMPRESSED_MOST = 1;
        public static final int COMPRESSED = 2;
        public static final int COMPRESSED_FASTER = 3;
        public static final int COMPRESSED_FASTEST = 4;
        public static final int NO_DATA_NO_CRC = 8;
        public static final int NO_DATA = 9;
    }

    /**
     * The known values for HostOs.
     */
    public static class HostOs {

        /**
         * Constant value {@value}.
         */
        public static final int DOS = 0;

        /**
         * Constant value {@value}.
         */
        public static final int PRIMOS = 1;

        /**
         * Constant value {@value}.
         */
        public static final int UNIX = 2;

        /**
         * Constant value {@value}.
         */
        public static final int AMIGA = 3;

        /**
         * Constant value {@value}.
         */
        public static final int MAC_OS = 4;

        /**
         * Constant value {@value}.
         */
        public static final int OS_2 = 5;

        /**
         * Constant value {@value}.
         */
        public static final int APPLE_GS = 6;

        /**
         * Constant value {@value}.
         */
        public static final int ATARI_ST = 7;

        /**
         * Constant value {@value}.
         */
        public static final int NEXT = 8;

        /**
         * Constant value {@value}.
         */
        public static final int VAX_VMS = 9;

        /**
         * Constant value {@value}.
         */
        public static final int WIN95 = 10;

        /**
         * Constant value {@value}.
         */
        public static final int WIN32 = 11;

        /**
         * Constructs a new instance.
         *
         * @deprecated Will be private in the next major release.
         */
        @Deprecated
        public HostOs() {
            // empty
        }
    }
}
