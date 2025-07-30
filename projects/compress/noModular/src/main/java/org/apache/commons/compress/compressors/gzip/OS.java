package org.apache.commons.compress.compressors.gzip;

/**
 * Enumerates OS types.
 * <ul>
 * <li>0 - FAT filesystem (MS-DOS, OS/2, NT/Win32)</li>
 * <li>1 - Amiga</li>
 * <li>2 - VMS (or OpenVMS)</li>
 * <li>3 - Unix</li>
 * <li>4 - VM/CMS</li>
 * <li>5 - Atari TOS</li>
 * <li>6 - HPFS filesystem (OS/2, NT)</li>
 * <li>7 - Macintosh</li>
 * <li>8 - Z-System</li>
 * <li>9 - CP/M</li>
 * <li>10 - TOPS-20</li>
 * <li>11 - NTFS filesystem (NT)</li>
 * <li>12 - QDOS</li>
 * <li>13 - Acorn RISCOS</li>
 * <li>255 - unknown</li>
 * </ul>
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc1952#page-7">RFC 1952: GZIP File Format Specification - OS (Operating System)</a>
 * @since 1.28.0
 */
public enum OS {

    /**
     * 13: Acorn RISCOS.
     */
    ACORN_RISCOS(GzipParameters.OS_ACORN_RISCOS),

    /**
     * 1: Amiga.
     */
    AMIGA(GzipParameters.OS_AMIGA),

    /**
     * 5: Atari TOS.
     */
    ATARI_TOS(GzipParameters.OS_ATARI_TOS),

    /**
     * 9: CP/M.
     */
    CPM(GzipParameters.OS_CPM),

    // @formatter:off
    /**
     * 0: FAT filesystem (MS-DOS, OS/2, NT/Win32).
     */
    FAT(GzipParameters.OS_FAT),

    /**
     * 6: HPFS filesystem (OS/2, NT).
     */
    HPFS(GzipParameters.OS_HPFS),

    /**
     * 7: Macintosh.
     */
    MACINTOSH(GzipParameters.OS_MACINTOSH),

    /**
     * 11: NTFS filesystem (NT).
     */
    NTFS(GzipParameters.OS_NTFS),

    /**
     * 12: QDOS.
     */
    QDOS(GzipParameters.OS_QDOS),

    /**
     * 10: TOPS-20.
     */
    TOPS_20(GzipParameters.OS_TOPS_20),

    /**
     * 3: Unix.
     */
    UNIX(GzipParameters.OS_UNIX),

    /**
     * 255: unknown.
     */
    UNKNOWN(GzipParameters.OS_UNKNOWN),

    /**
     * 4: VM/CMS.
     */
    VM_CMS(GzipParameters.OS_VM_CMS),

    /**
     * 2: VMS (or OpenVMS).
     */
    VMS(GzipParameters.OS_VMS),

    /**
     * 8: Z-System.
     */
    Z_SYSTEM(GzipParameters.OS_Z_SYSTEM);
    // @formatter:on

    /**
     * Gets the {@link OS} matching the given code.
     *
     * @param code an OS or {@link #UNKNOWN} for no match.
     * @return a {@link OS}.
     */
    public static OS from(final int code) {
        switch (code) {
            case GzipParameters.OS_ACORN_RISCOS:
                return ACORN_RISCOS;
            case GzipParameters.OS_AMIGA:
                return AMIGA;
            case GzipParameters.OS_ATARI_TOS:
                return ATARI_TOS;
            case GzipParameters.OS_CPM:
                return CPM;
            case GzipParameters.OS_FAT:
                return FAT;
            case GzipParameters.OS_HPFS:
                return HPFS;
            case GzipParameters.OS_MACINTOSH:
                return MACINTOSH;
            case GzipParameters.OS_NTFS:
                return NTFS;
            case GzipParameters.OS_QDOS:
                return QDOS;
            case GzipParameters.OS_TOPS_20:
                return TOPS_20;
            case GzipParameters.OS_UNIX:
                return UNIX;
            case GzipParameters.OS_UNKNOWN:
                return UNKNOWN;
            case GzipParameters.OS_VM_CMS:
                return VM_CMS;
            case GzipParameters.OS_VMS:
                return VMS;
            case GzipParameters.OS_Z_SYSTEM:
                return Z_SYSTEM;
            default:
                return UNKNOWN;
        }
    }

    final int type;

    /**
     * Constructs a new instance.
     *
     * @param type the OS type.
     */
    OS(final int type) {
        this.type = type;
    }

    /**
     * Gets the OS type.
     *
     * @return the OS type.
     */
    public int type() {
        return type;
    }

}
