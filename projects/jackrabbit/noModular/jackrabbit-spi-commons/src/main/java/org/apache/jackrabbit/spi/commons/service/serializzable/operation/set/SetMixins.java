package org.apache.jackrabbit.spi.commons.service.serializzable.operation.set;

import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.Operation;

import javax.jcr.RepositoryException;

public class SetMixins implements Operation {

    private final NodeId nodeId;

    private final Name[] mixinNodeTypeNames;

    public SetMixins(NodeId nodeId, Name[] mixinNodeTypeNames) {
        this.nodeId = nodeId;
        this.mixinNodeTypeNames = mixinNodeTypeNames;
    }

    /**
     * {@inheritDoc}
     */
    public void replay(Batch batch) throws RepositoryException {
        batch.setMixins(nodeId, mixinNodeTypeNames);
    }
}
