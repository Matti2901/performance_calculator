package org.apache.jackrabbit.spi.commons.service.serializzable.operation;

import org.apache.jackrabbit.spi.Batch;

import javax.jcr.RepositoryException;
import java.io.Serializable;

public interface Operation extends Serializable {

    /**
     * Replays this operation on the given <code>batch</code>.
     *
     * @param batch the batch.
     * @throws RepositoryException if an error occurs replaying the
     *                             operation.
     */
    public void replay(Batch batch) throws RepositoryException;
}
