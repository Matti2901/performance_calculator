package org.apache.commons.compress.changes;

/**
 * Enumerates types of changes.
 */
public enum ChangeType {

    /**
     * Delete.
     */
    DELETE,

    /**
     * Add.
     */
    ADD,

    /**
     * Not used.
     */
    MOVE,

    /**
     * Delete directory.
     */
    DELETE_DIR
}
