package org.apache.jackrabbit.core.persistence.pool.blob;

import org.apache.jackrabbit.core.fs.FileSystem;
import org.apache.jackrabbit.core.id.PropertyId;
import org.apache.jackrabbit.core.persistence.pool.BundleDbPersistenceManager;
import org.apache.jackrabbit.core.persistence.util.FileSystemBLOBStore;

/**
 * own implementation of the filesystem blob store that uses a different
 * blob-id scheme.
 */
public class FSBlobStore extends FileSystemBLOBStore implements CloseableBLOBStore {

    private final BundleDbPersistenceManager bundleDbPersistenceManager;
    private FileSystem fs;

    public FSBlobStore(BundleDbPersistenceManager bundleDbPersistenceManager, FileSystem fs) {
        super(fs);
        this.bundleDbPersistenceManager = bundleDbPersistenceManager;
        this.fs = fs;
    }

    public String createId(PropertyId id, int index) {
        return bundleDbPersistenceManager.buildBlobFilePath(null, id, index).toString();
    }

    public void close() {
        try {
            fs.close();
            fs = null;
        } catch (Exception e) {
            // ignore
        }
    }
}
