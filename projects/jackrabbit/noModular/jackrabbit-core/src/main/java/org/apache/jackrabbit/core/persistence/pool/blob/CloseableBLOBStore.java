package org.apache.jackrabbit.core.persistence.pool.blob;

import org.apache.jackrabbit.core.persistence.util.BLOBStore;

/**
 * Helper interface for closeable stores
 */
public interface CloseableBLOBStore extends BLOBStore {
    void close();
}
