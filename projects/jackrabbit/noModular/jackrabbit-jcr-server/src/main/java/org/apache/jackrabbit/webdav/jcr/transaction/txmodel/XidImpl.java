package org.apache.jackrabbit.webdav.jcr.transaction.txmodel;

import javax.transaction.xa.Xid;

/**
 * Private class implementing Xid interface.
 */
public class XidImpl implements Xid {

    private final String id;

    /**
     * Create a new Xid
     *
     * @param id
     */
    public XidImpl(String id) {
        this.id = id;
    }

    /**
     * @return 1
     * @see Xid#getFormatId()
     */
    public int getFormatId() {
        // todo: define reasonable format id
        return 1;
    }

    /**
     * @return an empty byte array.
     * @see Xid#getBranchQualifier()
     */
    public byte[] getBranchQualifier() {
        return new byte[0];
    }

    /**
     * @return id as byte array
     * @see Xid#getGlobalTransactionId()
     */
    public byte[] getGlobalTransactionId() {
        return id.getBytes();
    }
}
