package org.apache.jackrabbit.webdav.jcr.transaction.txmodel;

import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.transaction.TxActiveLock;

/**
 * Abstract transaction covering functionally to both implementations.
 */
abstract class AbstractTransaction extends TransactionMap implements Transaction {

    private final DavResourceLocator locator;
    private final TxActiveLock lock;

    public AbstractTransaction(DavResourceLocator locator, TxActiveLock lock) {
        this.locator = locator;
        this.lock = lock;
    }

    //----------------------------------------------------< Transaction >---

    /**
     * @see #getLock()
     */
    public TxActiveLock getLock() {
        return lock;
    }

    /**
     * @see #getId()
     */
    public String getId() {
        return lock.getToken();
    }

    /**
     * @see #getResourcePath()
     */
    public String getResourcePath() {
        return locator.getResourcePath();
    }

    /**
     * @see #appliesToResource(DavResource)
     */
    public boolean appliesToResource(DavResource resource) {
        if (locator.isSameWorkspace(resource.getLocator())) {
            String lockResourcePath = getResourcePath();
            String resPath = resource.getResourcePath();

            while (!"".equals(resPath)) {
                if (lockResourcePath.equals(resPath)) {
                    return true;
                }
                resPath = Text.getRelativeParent(resPath, 1);
            }
        }
        return false;
    }
}
