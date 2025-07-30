package org.apache.jackrabbit.spi.commons.service.serializzable.operation;

import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;

import javax.jcr.RepositoryException;

public class Move implements Operation {

    private final NodeId srcNodeId;

    private final NodeId destParentNodeId;

    private final Name destName;

    public Move(NodeId srcNodeId, NodeId destParentNodeId, Name destName) {
        this.srcNodeId = srcNodeId;
        this.destParentNodeId = destParentNodeId;
        this.destName = destName;
    }

    /**
     * {@inheritDoc}
     */
    public void replay(Batch batch) throws RepositoryException {
        batch.move(srcNodeId, destParentNodeId, destName);
    }
}
