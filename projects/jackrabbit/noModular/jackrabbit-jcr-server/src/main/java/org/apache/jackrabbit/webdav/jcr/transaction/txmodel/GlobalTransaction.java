package org.apache.jackrabbit.webdav.jcr.transaction.txmodel;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavServletResponse;
import org.apache.jackrabbit.webdav.WebdavResponse;
import org.apache.jackrabbit.webdav.jcr.transaction.TxLockManagerImpl;
import org.apache.jackrabbit.webdav.transaction.TransactionResource;
import org.apache.jackrabbit.webdav.transaction.TxActiveLock;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * Global transaction
 */
public class GlobalTransaction extends AbstractTransaction {

    private Xid xid;

    public GlobalTransaction(DavResourceLocator locator, TxActiveLock lock) {
        super(locator, lock);
        xid = new XidImpl(lock.getToken());
    }

    //----------------------------------------------------< Transaction >---

    /**
     * @see Transaction#isLocal()
     */
    public boolean isLocal() {
        return false;
    }

    /**
     * @see Transaction#start(TransactionResource)
     */
    public void start(TransactionResource resource) throws DavException {
        XAResource xaRes = getXAResource(resource);
        try {
            xaRes.setTransactionTimeout((int) getLock().getTimeout() / 1000);
            xaRes.start(xid, XAResource.TMNOFLAGS);
        } catch (XAException e) {
            throw new DavException(DavServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    /**
     * @see Transaction#commit(TransactionResource)
     */
    public void commit(TransactionResource resource) throws DavException {
        XAResource xaRes = getXAResource(resource);
        try {
            xaRes.commit(xid, false);
            removeLocalTxReferences(resource);
        } catch (XAException e) {
            throw new DavException(DavServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    /**
     * @see Transaction#rollback(TransactionResource)
     */
    public void rollback(TransactionResource resource) throws DavException {
        XAResource xaRes = getXAResource(resource);
        try {
            xaRes.rollback(xid);
            removeLocalTxReferences(resource);
        } catch (XAException e) {
            throw new DavException(DavServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    //-------------------------------------------------< TransactionMap >---
    @Override
    public Transaction putTransaction(String key, Transaction value) throws DavException {
        if (!(value instanceof LocalTransaction)) {
            throw new DavException(WebdavResponse.SC_PRECONDITION_FAILED, "Attempt to nest global transaction into a global one.");
        }
        return super.put(key, value);
    }

    //--------------------------------------------------------< private >---
    private XAResource getXAResource(TransactionResource resource) throws DavException {
        /*
        // commented, since server should be jackrabbit independent
        Session session = resource.getSession().getRepositorySession();
        if (session instanceof XASession) {
        return ((XASession)session).getXAResource();
        } else {
        throw new DavException(DavServletResponse.SC_FORBIDDEN);
        }
        */
        throw new DavException(DavServletResponse.SC_FORBIDDEN);
    }

    private void removeLocalTxReferences(TransactionResource resource) {
        for (Object o : values()) {
            Transaction tx = (Transaction) o;
            TxLockManagerImpl.removeReferences(tx, this, resource);
        }
    }
}
