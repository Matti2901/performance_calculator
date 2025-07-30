package org.apache.jackrabbit.webdav.jcr.transaction.txmodel;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.WebdavResponse;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.transaction.TxLockManagerImpl;
import org.apache.jackrabbit.webdav.transaction.TransactionResource;
import org.apache.jackrabbit.webdav.transaction.TxActiveLock;

import javax.jcr.Item;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

/**
 * Local transaction
 */
public final class LocalTransaction extends AbstractTransaction {

    public LocalTransaction(DavResourceLocator locator, TxActiveLock lock) {
        super(locator, lock);
    }

    //----------------------------------------------------< Transaction >---

    /**
     * @see Transaction#isLocal()
     */
    public boolean isLocal() {
        return true;
    }

    /**
     * @see Transaction#start(TransactionResource)
     */
    public void start(TransactionResource resource) throws DavException {
        try {
            // make sure, the given resource represents an existing repository item
            if (!TxLockManagerImpl.getRepositorySession(resource).itemExists(resource.getLocator().getRepositoryPath())) {
                throw new DavException(DavServletResponse.SC_CONFLICT, "Unable to start local transaction: no repository item present at " + getResourcePath());
            }
        } catch (RepositoryException e) {
            TxLockManagerImpl.log.error("Unexpected error: " + e.getMessage());
            throw new JcrDavException(e);
        }
    }

    /**
     * @see Transaction#commit(TransactionResource)
     */
    public void commit(TransactionResource resource) throws DavException {
        try {
            getItem(resource).save();
        } catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    /**
     * @see Transaction#rollback(TransactionResource)
     */
    public void rollback(TransactionResource resource) throws DavException {
        try {
            getItem(resource).refresh(false);
        } catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    //-------------------------------------------------< TransactionMap >---

    /**
     * Always throws <code>DavException</code>.
     *
     * @see TransactionMap#putTransaction(String, Transaction)
     */
    @Override
    public Transaction putTransaction(String key, Transaction value) throws DavException {
        throw new DavException(WebdavResponse.SC_PRECONDITION_FAILED, "Attempt to nest a new transaction into a local one.");
    }

    //--------------------------------------------------------< private >---

    /**
     * Retrieve the repository item from the given transaction resource.
     *
     * @param resource
     * @return
     * @throws PathNotFoundException
     * @throws RepositoryException
     * @throws DavException
     */
    private Item getItem(TransactionResource resource) throws PathNotFoundException, RepositoryException, DavException {
        String itemPath = resource.getLocator().getRepositoryPath();
        return TxLockManagerImpl.getRepositorySession(resource).getItem(itemPath);
    }
}
