package org.apache.jackrabbit.spi.commons.service.serializzable.operation.add;

import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.Operation;

import javax.jcr.RepositoryException;

public class AddNode implements Operation {

    private final NodeId parentId;

    private final Name nodeName;

    private final Name nodetypeName;

    private final String uuid;

    public AddNode(NodeId parentId, Name nodeName, Name nodetypeName, String uuid) {
        this.parentId = parentId;
        this.nodeName = nodeName;
        this.nodetypeName = nodetypeName;
        this.uuid = uuid;
    }

    /**
     * {@inheritDoc}
     */
    public void replay(Batch batch) throws RepositoryException {
        batch.addNode(parentId, nodeName, nodetypeName, uuid);
    }
}
