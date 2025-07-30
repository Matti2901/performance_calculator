package org.apache.jackrabbit.spi.commons.service.serializzable.operation.set;

import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.Tree;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.Operation;

import javax.jcr.RepositoryException;

public class SetTree implements Operation {

    private final NodeId parentId;

    private final Tree contentTree;

    public SetTree(NodeId parentId, Tree contentTree) {
        this.parentId = parentId;
        this.contentTree = contentTree;
    }

    /**
     * {@inheritDoc}
     */
    public void replay(Batch batch) throws RepositoryException {
        batch.setTree(parentId, contentTree);
    }
}
