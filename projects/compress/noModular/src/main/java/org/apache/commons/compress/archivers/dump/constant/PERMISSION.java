package org.apache.commons.compress.archivers.dump.constant;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Enumerates permissions with values.
 */
public enum PERMISSION {
    // Note: The arguments are octal values

    /**
     * Permission SETUID (octal value 04000).
     */
    SETUID(04000),

    /**
     * Permission SETGUI (octal value 02000).
     */
    SETGUI(02000),

    /**
     * Permission STICKY (octal value 01000).
     */
    STICKY(01000),

    /**
     * Permission USER_READ (octal value 00400).
     */
    USER_READ(00400),

    /**
     * Permission USER_WRITE (octal value 00200).
     */
    USER_WRITE(00200),

    /**
     * Permission USER_EXEC (octal value 00100).
     */
    USER_EXEC(00100),

    /**
     * Permission GROUP_READ (octal value 00040).
     */
    GROUP_READ(00040),

    /**
     * Permission GROUP_WRITE (octal value 00020).
     */
    GROUP_WRITE(00020),

    /**
     * Permission 00020 (octal value 00010).
     */
    GROUP_EXEC(00010),

    /**
     * Permission WORLD_READ (octal value 00004).
     */
    WORLD_READ(00004),

    /**
     * Permission WORLD_WRITE (octal value 00002).
     */
    WORLD_WRITE(00002),

    /**
     * Permission WORLD_EXEC (octal value 00001).
     */
    WORLD_EXEC(00001);

    /**
     * Finds a matching set of enumeration values for the given code.
     *
     * @param code a code.
     * @return a Set of values, never null.
     */
    public static Set<PERMISSION> find(final int code) {
        final Set<PERMISSION> set = new HashSet<>();
        for (final PERMISSION p : values()) {
            if ((code & p.code) == p.code) {
                set.add(p);
            }
        }
        if (set.isEmpty()) {
            return Collections.emptySet();
        }
        return EnumSet.copyOf(set);
    }

    private final int code;

    PERMISSION(final int code) {
        this.code = code;
    }
}
