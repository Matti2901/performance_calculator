package org.apache.jackrabbit.core.version.manager.base;

import org.apache.jackrabbit.core.state.ItemStateException;
import org.apache.jackrabbit.core.state.LocalItemStateManager;
import org.apache.jackrabbit.core.version.lock.VersioningLock;
import org.apache.jackrabbit.core.version.manager.InternalVersionManagerBase;

import javax.jcr.RepositoryException;

/**
 * Helper for managing write operations.
 */
public class WriteOperation {

    private final InternalVersionManagerBase internalVersionManagerBase;
    /**
     * Flag for successful completion of the write operation.
     */
    private boolean success = false;

    private final VersioningLock.WriteLock lock;

    public WriteOperation(InternalVersionManagerBase internalVersionManagerBase, VersioningLock.WriteLock lock) {
        this.internalVersionManagerBase = internalVersionManagerBase;
        this.lock = lock;
    }

    /**
     * Saves the pending operations in the {@link LocalItemStateManager}.
     *
     * @throws ItemStateException  if the pending state is invalid
     * @throws RepositoryException if the pending state could not be persisted
     */
    public void save() throws ItemStateException, RepositoryException {
        internalVersionManagerBase.stateMgr.update();
        success = true;
    }

    /**
     * Closes the write operation. The pending operations are canceled
     * if they could not be properly saved. Finally the write lock is
     * released.
     */
    public void close() {
        try {
            if (!success) {
                // update operation failed, cancel all modifications
                internalVersionManagerBase.stateMgr.cancel();
            }
        } finally {
            lock.release();
        }
    }
}
