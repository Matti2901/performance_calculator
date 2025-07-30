package org.apache.jackrabbit.server.remoting.davex.factory;

import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.transaction.TxLockManagerImpl;
import org.apache.jackrabbit.webdav.observation.SubscriptionManager;

import javax.jcr.Item;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Resource factory used to make sure that the .json extension was properly
 * interpreted.
 */
public class ResourceFactoryImpl extends org.apache.jackrabbit.webdav.jcr.DavResourceFactoryImpl {

    /**
     * Create a new <code>DavResourceFactoryImpl</code>.
     *
     * @param txMgr
     * @param subsMgr
     */
    public ResourceFactoryImpl(TxLockManagerImpl txMgr, SubscriptionManager subsMgr) {
        super(txMgr, subsMgr);
    }

    @Override
    protected Item getItem(JcrDavSession sessionImpl, DavResourceLocator locator) throws PathNotFoundException, RepositoryException {
        if (locator instanceof WrappingLocator && ((WrappingLocator) locator).isJsonRequest) {
            // check if the .json extension has been correctly interpreted.
            Session s = sessionImpl.getRepositorySession();
            try {
                if (s.itemExists(((WrappingLocator) locator).loc.getRepositoryPath())) {
                    // an item exists with the original calculated repo-path
                    // -> assume that the repository item path ends with .json
                    // or .depth.json. i.e. .json wasn't an extra extension
                    // appended to request the json-serialization of the node.
                    // -> change the flag in the WrappingLocator correspondingly.
                    ((WrappingLocator) locator).isJsonRequest = false;
                }
            } catch (RepositoryException e) {
                // if the unmodified repository path isn't valid (e.g. /a/b[2].5.json)
                // -> ignore.
            }
        }
        return super.getItem(sessionImpl, locator);
    }
}
