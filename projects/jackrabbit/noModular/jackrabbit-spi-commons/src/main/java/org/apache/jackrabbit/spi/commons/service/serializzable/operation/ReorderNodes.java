package org.apache.jackrabbit.spi.commons.service.serializzable.operation;

import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.NodeId;

import javax.jcr.RepositoryException;

public class ReorderNodes implements Operation {

    private final NodeId parentId;

    private final NodeId srcNodeId;

    private final NodeId beforeNodeId;

    public ReorderNodes(NodeId parentId, NodeId srcNodeId, NodeId beforeNodeId) {
        this.parentId = parentId;
        this.srcNodeId = srcNodeId;
        this.beforeNodeId = beforeNodeId;
    }

    /**
     * {@inheritDoc}
     */
    public void replay(Batch batch) throws RepositoryException {
        batch.reorderNodes(parentId, srcNodeId, beforeNodeId);
    }
}
