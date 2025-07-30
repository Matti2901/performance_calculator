package org.apache.jackrabbit.core.persistence.db.blob;

import org.apache.jackrabbit.core.id.PropertyId;
import org.apache.jackrabbit.core.persistence.db.DatabasePersistenceManager;
import org.apache.jackrabbit.core.persistence.util.BLOBStore;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbBLOBStore implements BLOBStore {
    private final DatabasePersistenceManager databasePersistenceManager;

    public DbBLOBStore(DatabasePersistenceManager databasePersistenceManager) {
        this.databasePersistenceManager = databasePersistenceManager;
    }

    /**
     * {@inheritDoc}
     */
    public String createId(PropertyId id, int index) {
        // the blobId is a simple string concatenation of id plus index
        StringBuilder sb = new StringBuilder();
        sb.append(id.toString());
        sb.append('[');
        sb.append(index);
        sb.append(']');
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public InputStream get(String blobId) throws Exception {
        synchronized (databasePersistenceManager.blobSelectSQL) {
            Statement stmt = databasePersistenceManager.executeStmt(databasePersistenceManager.blobSelectSQL, new Object[]{blobId});
            final ResultSet rs = stmt.getResultSet();
            if (!rs.next()) {
                databasePersistenceManager.closeResultSet(rs);
                throw new Exception("no such BLOB: " + blobId);
            }
            InputStream in = rs.getBinaryStream(1);
            if (in == null) {
                // some databases treat zero-length values as NULL;
                // return empty InputStream in such a case
                databasePersistenceManager.closeResultSet(rs);
                return new ByteArrayInputStream(new byte[0]);
            }

            /**
             * return an InputStream wrapper in order to
             * close the ResultSet when the stream is closed
             */
            return new FilterInputStream(in) {
                public void close() throws IOException {
                    in.close();
                    // now it's safe to close ResultSet
                    databasePersistenceManager.closeResultSet(rs);
                }
            };
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void put(String blobId, InputStream in, long size)
            throws Exception {
        Statement stmt = databasePersistenceManager.executeStmt(databasePersistenceManager.blobSelectExistSQL, new Object[]{blobId});
        ResultSet rs = stmt.getResultSet();
        // a BLOB exists if the result has at least one entry
        boolean exists = rs.next();
        databasePersistenceManager.closeResultSet(rs);

        String sql = (exists) ? databasePersistenceManager.blobUpdateSQL : databasePersistenceManager.blobInsertSQL;
        databasePersistenceManager.executeStmt(sql, new Object[]{new SizedInputStream(in, size), blobId});
    }

    /**
     * {@inheritDoc}
     */
    public synchronized boolean remove(String blobId) throws Exception {
        Statement stmt = databasePersistenceManager.executeStmt(databasePersistenceManager.blobDeleteSQL, new Object[]{blobId});
        return stmt.getUpdateCount() == 1;
    }
}
