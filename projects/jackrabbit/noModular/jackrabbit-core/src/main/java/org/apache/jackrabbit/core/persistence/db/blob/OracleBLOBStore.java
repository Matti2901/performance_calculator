package org.apache.jackrabbit.core.persistence.db.blob;

import org.apache.jackrabbit.core.persistence.db.OraclePersistenceManager;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.Statement;

//--------------------------------------------------------< inner classes >
public class OracleBLOBStore extends DbBLOBStore {
    private final OraclePersistenceManager oraclePersistenceManager;

    public OracleBLOBStore(OraclePersistenceManager oraclePersistenceManager) {
        super(null);
        this.oraclePersistenceManager = oraclePersistenceManager;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void put(String blobId, InputStream in, long size)
            throws Exception {
        Statement stmt = oraclePersistenceManager.executeStmt(oraclePersistenceManager.blobSelectExistSQL, new Object[]{blobId});
        ResultSet rs = stmt.getResultSet();
        // a BLOB exists if the result has at least one entry
        boolean exists = rs.next();
        oraclePersistenceManager.closeResultSet(rs);

        Blob blob = null;
        try {
            String sql = (exists) ? oraclePersistenceManager.blobUpdateSQL : oraclePersistenceManager.blobInsertSQL;
            blob = oraclePersistenceManager.createTemporaryBlob(in);
            oraclePersistenceManager.executeStmt(sql, new Object[]{blob, blobId});
        } finally {
            if (blob != null) {
                try {
                    oraclePersistenceManager.freeTemporaryBlob(blob);
                } catch (Exception ignore) {
                }
            }
        }
    }
}
