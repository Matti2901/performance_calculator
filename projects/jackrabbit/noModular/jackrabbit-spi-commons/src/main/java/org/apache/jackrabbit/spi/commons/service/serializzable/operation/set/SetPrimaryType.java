package org.apache.jackrabbit.spi.commons.service.serializzable.operation.set;

import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.Operation;

import javax.jcr.RepositoryException;

public class SetPrimaryType implements Operation {

    private final NodeId nodeId;

    private final Name primaryNodeTypeName;

    public SetPrimaryType(NodeId nodeId, Name primaryNodeTypeName) {
        this.nodeId = nodeId;
        this.primaryNodeTypeName = primaryNodeTypeName;
    }

    /**
     * {@inheritDoc}
     */
    public void replay(Batch batch) throws RepositoryException {
        batch.setPrimaryType(nodeId, primaryNodeTypeName);
    }
}
