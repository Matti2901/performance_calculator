package org.apache.jackrabbit.webdav.jcr.transaction.txmodel;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.transaction.TransactionResource;
import org.apache.jackrabbit.webdav.transaction.TxActiveLock;

/**
 * Internal <code>Transaction</code> interface
 */
public interface Transaction {

    TxActiveLock getLock();

    /**
     * @return the id of this transaction.
     */
    String getId();

    /**
     * @return path of the lock holding resource
     */
    String getResourcePath();

    /**
     * @param resource
     * @return true if the lock defined by this transaction applies to the
     * given resource, either due to the resource holding that lock or due
     * to a deep lock hold by any ancestor resource.
     */
    boolean appliesToResource(DavResource resource);

    /**
     * @return true if this transaction is used to allow for transient changes
     * on the underlying repository, that may be persisted with the final
     * UNLOCK request only.
     */
    boolean isLocal();

    /**
     * Start this transaction.
     *
     * @param resource
     * @throws DavException if an error occurs.
     */
    void start(TransactionResource resource) throws DavException;

    /**
     * Commit this transaction
     *
     * @param resource
     * @throws DavException if an error occurs.
     */
    void commit(TransactionResource resource) throws DavException;

    /**
     * Rollback this transaction.
     *
     * @param resource
     * @throws DavException if an error occurs.
     */
    void rollback(TransactionResource resource) throws DavException;
}
