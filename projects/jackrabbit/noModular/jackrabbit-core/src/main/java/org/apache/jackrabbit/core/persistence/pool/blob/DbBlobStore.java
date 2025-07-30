package org.apache.jackrabbit.core.persistence.pool.blob;

import org.apache.jackrabbit.core.id.PropertyId;
import org.apache.jackrabbit.core.persistence.pool.BundleDbPersistenceManager;
import org.apache.jackrabbit.core.util.db.DbUtility;
import org.apache.jackrabbit.core.util.db.StreamWrapper;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of a blob store that stores the data inside the database
 */
public class DbBlobStore implements CloseableBLOBStore {

    private final BundleDbPersistenceManager bundleDbPersistenceManager;
    protected String blobInsertSQL;
    protected String blobUpdateSQL;
    protected String blobSelectSQL;
    protected String blobSelectExistSQL;
    protected String blobDeleteSQL;

    public DbBlobStore(BundleDbPersistenceManager bundleDbPersistenceManager) throws SQLException {
        this.bundleDbPersistenceManager = bundleDbPersistenceManager;
        blobInsertSQL = "insert into " + bundleDbPersistenceManager.getSchemaObjectPrefix() + "BINVAL (BINVAL_DATA, BINVAL_ID) values (?, ?)";
        blobUpdateSQL = "update " + bundleDbPersistenceManager.getSchemaObjectPrefix() + "BINVAL set BINVAL_DATA = ? where BINVAL_ID = ?";
        blobSelectSQL = "select BINVAL_DATA from " + bundleDbPersistenceManager.getSchemaObjectPrefix() + "BINVAL where BINVAL_ID = ?";
        blobSelectExistSQL = "select 1 from " + bundleDbPersistenceManager.getSchemaObjectPrefix() + "BINVAL where BINVAL_ID = ?";
        blobDeleteSQL = "delete from " + bundleDbPersistenceManager.getSchemaObjectPrefix() + "BINVAL where BINVAL_ID = ?";
    }

    /**
     * {@inheritDoc}
     */
    public String createId(PropertyId id, int index) {
        StringBuilder buf = new StringBuilder();
        buf.append(id.getParentId().toString());
        buf.append('.');
        buf.append(bundleDbPersistenceManager.getNsIndex().stringToIndex(id.getName().getNamespaceURI()));
        buf.append('.');
        buf.append(bundleDbPersistenceManager.getNameIndex().stringToIndex(id.getName().getLocalName()));
        buf.append('.');
        buf.append(index);
        return buf.toString();
    }

    /**
     * {@inheritDoc}
     */
    public InputStream get(String blobId) throws Exception {
        ResultSet rs = null;
        boolean close = true;
        try {
            rs = bundleDbPersistenceManager.conHelper.exec(blobSelectSQL, new Object[]{blobId}, false, 0);
            if (!rs.next()) {
                throw new Exception("no such BLOB: " + blobId);
            }

            InputStream in = rs.getBinaryStream(1);
            if (in == null) {
                // some databases treat zero-length values as NULL;
                // return empty InputStream in such a case
                return new ByteArrayInputStream(new byte[0]);
            }

            // return an InputStream wrapper in order to close the ResultSet when the stream is closed
            close = false;
            final ResultSet rs2 = rs;
            return new FilterInputStream(in) {

                public void close() throws IOException {
                    try {
                        in.close();
                    } finally {
                        // now it's safe to close ResultSet
                        DbUtility.close(rs2);
                    }
                }
            };
        } finally {
            if (close) {
                DbUtility.close(rs);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void put(String blobId, InputStream in, long size)
            throws Exception {
        ResultSet rs = null;
        boolean exists;
        try {
            rs = bundleDbPersistenceManager.conHelper.exec(blobSelectExistSQL, new Object[]{blobId}, false, 0);
            // a BLOB exists if the result has at least one entry
            exists = rs.next();
        } finally {
            DbUtility.close(rs);
        }
        String sql = (exists) ? blobUpdateSQL : blobInsertSQL;
        Object[] params = new Object[]{new StreamWrapper(in, size), blobId};
        bundleDbPersistenceManager.conHelper.exec(sql, params);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized boolean remove(String blobId) throws Exception {
        return bundleDbPersistenceManager.conHelper.update(blobDeleteSQL, new Object[]{blobId}) == 1;
    }

    public void close() {
        // closing the database resources of this blobstore is left to the
        // owning BundleDbPersistenceManager
    }
}
