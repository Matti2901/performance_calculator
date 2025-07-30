package org.apache.commons.compress.archivers.arj.mainheaderparameter;

public class MainParameter {
    public static final class Flags {
        public static final int GARBLED = 0x01;
        public static final int OLD_SECURED_NEW_ANSI_PAGE = 0x02;
        public static final int VOLUME = 0x04;
        public  static final int ARJPROT = 0x08;
        public  static final int PATHSYM = 0x10;
        public static final int BACKUP = 0x20;
        public  static final int SECURED = 0x40;
        public  static final int ALTNAME = 0x80;
    }

    public static final class HostOS {
        public  static final int MS_DOS = 0;
        public  static final int PRIMOS = 1;
        public  static final int UNIX = 2;
        public  static final int AMIGA = 3;
        public   static final int MAC_OS = 4;
        public  static final int OS2 = 5;
        public  static final int APPLE_GS = 6;
        public   static final int ATARI_ST = 7;
        public  static final int NeXT = 8;
        public  static final int VAX_VMS = 9;
        public  static final int WIN95 = 10;
        public  static final int WIN32 = 11;
    }
}
