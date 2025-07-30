package org.apache.jackrabbit.webdav.jcr.transaction.txmodel;

import org.apache.jackrabbit.webdav.DavException;

import java.util.HashMap;

/**
 *
 */
public class TransactionMap extends HashMap<String, Transaction> {

    public Transaction get(String key) {
        Transaction tx = null;
        if (containsKey(key)) {
            tx = super.get(key);
        }
        return tx;
    }

    public Transaction putTransaction(String key, Transaction value) throws DavException {
        // any global and local transactions allowed.
        return super.put(key, value);
    }
}
