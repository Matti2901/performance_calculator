package org.apache.jackrabbit.jcr2spi.operation.set.tree;

import org.apache.jackrabbit.jcr2spi.operation.IgnoreOperation;
import org.apache.jackrabbit.jcr2spi.operation.Operation;
import org.apache.jackrabbit.jcr2spi.operation.creation.AddNode;
import org.apache.jackrabbit.jcr2spi.state.ItemStateValidator;
import org.apache.jackrabbit.jcr2spi.state.NodeState;
import org.apache.jackrabbit.spi.Name;

import javax.jcr.RepositoryException;

/**
 * Inner class for adding a protected node.
 */
public final class InternalAddNode extends AddNode implements IgnoreOperation {
    /**
     * Options that must not be violated for a successful set policy operation.
     */
    public final static int ADD_NODE_OPTIONS = ItemStateValidator.CHECK_ACCESS |
            ItemStateValidator.CHECK_LOCK |
            ItemStateValidator.CHECK_COLLISION |
            ItemStateValidator.CHECK_VERSIONING;

   public InternalAddNode(NodeState parentState, Name nodeName, Name nodeTypeName, String uuid) throws RepositoryException {
        super(parentState, nodeName, nodeTypeName, uuid, ADD_NODE_OPTIONS);
    }

    public static Operation create(NodeState parentState, Name nodeName, Name nodeTypeName, String uuid) throws RepositoryException {
        assertChildNodeEntries(parentState);
        InternalAddNode an = new InternalAddNode(parentState, nodeName, nodeTypeName, uuid);
        return an;
    }
}
