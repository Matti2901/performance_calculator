/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.spi.commons.batch.change.consolidating;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PathFactory;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.Tree;
import org.apache.jackrabbit.spi.commons.batch.change.AbstractChangeLog;
import org.apache.jackrabbit.spi.commons.batch.change.ChangeLog;
import org.apache.jackrabbit.spi.commons.batch.change.ChangeLogImpl;
import org.apache.jackrabbit.spi.commons.batch.change.consolidating.backward.OperationsBackwardWithSentinel;
import org.apache.jackrabbit.spi.commons.batch.change.consolidating.cancel.CancelableOperation;
import org.apache.jackrabbit.spi.commons.batch.change.consolidating.cancel.CancelableOperations;
import org.apache.jackrabbit.spi.commons.batch.operation.Operation;
import org.apache.jackrabbit.spi.commons.batch.operation.Operations;
import org.apache.jackrabbit.spi.commons.name.path.builder.PathFactoryImpl;

/**
 * A {@link ChangeLog} implementation which does basic consolidation on its
 * {@link Operation Operation}s. That is, cancelling
 * operations are removed if possible. In general this is not possible across
 * {@link Operations.Move move} operations. The individual
 * {@link CancelableOperation CancelableOperation} implementations document their behavior
 * concerning cancellation.
 */
public class ConsolidatingChangeLog extends AbstractChangeLog<CancelableOperation> {
    private static final PathFactory PATH_FACTORY = PathFactoryImpl.getInstance();

    /**
     * Create a new instance of a consolidating change log.
     */
    public ConsolidatingChangeLog() {
        super();
    }

    /**
     * Create a {@link Path} from the {@link NodeId} of a parent and the {@link Name} of a
     * child.
     * @param parentId  node id of the parent
     * @param name  name of the child
     * @return  the path of the item <code>name</code> or <code>null</code> if <code>parentId</code>'s
     * path is not absolute
     * @throws RepositoryException
     */
    public static Path getPath(NodeId parentId, Name name) throws RepositoryException {
        Path parent = parentId.getPath();
        if (!parent.isAbsolute()) {
            return null;
        }

        return PATH_FACTORY.create(parent, name, true);
    }

    /**
     * Determine the {@link Path} from an {@link ItemId}.
     * @param itemId
     * @return  path of the item <code>itemId</code> or <code>null</code> if <code>itemId</code>'s
     * path is not absolute
     */
    public static Path getPath(ItemId itemId) {
        Path path = itemId.getPath();
        if (path != null && !path.isAbsolute()) {
            return null;
        }
        return path;
    }

    // -----------------------------------------------------< ChangeLog >---

    public void addNode(NodeId parentId, Name nodeName, Name nodetypeName, String uuid)
            throws RepositoryException {

        addOperation(CancelableOperations.addNode(parentId, nodeName, nodetypeName, uuid));
    }

    public void addProperty(NodeId parentId, Name propertyName, QValue value) throws RepositoryException {
        addOperation(CancelableOperations.addProperty(parentId, propertyName, value));
    }

    public void addProperty(NodeId parentId, Name propertyName, QValue[] values) throws RepositoryException {
        addOperation(CancelableOperations.addProperty(parentId, propertyName, values));
    }

    public void move(NodeId srcNodeId, NodeId destParentNodeId, Name destName) throws RepositoryException {
        addOperation(CancelableOperations.move(srcNodeId, destParentNodeId, destName));
    }

    public void remove(ItemId itemId) throws RepositoryException {
        addOperation(CancelableOperations.remove(itemId));
    }

    public void reorderNodes(NodeId parentId, NodeId srcNodeId, NodeId beforeNodeId) throws RepositoryException {
        addOperation(CancelableOperations.reorderNodes(parentId, srcNodeId, beforeNodeId));
    }

    public void setMixins(NodeId nodeId, Name[] mixinNodeTypeNames) throws RepositoryException {
        addOperation(CancelableOperations.setMixins(nodeId, mixinNodeTypeNames));
    }

    public void setPrimaryType(NodeId nodeId, Name primaryNodeTypeName) throws RepositoryException {
        addOperation(CancelableOperations.setPrimaryType(nodeId, primaryNodeTypeName));
    }

    public void setValue(PropertyId propertyId, QValue value) throws RepositoryException {
        addOperation(CancelableOperations.setValue(propertyId, value));
    }

    public void setValue(PropertyId propertyId, QValue[] values) throws RepositoryException {
        addOperation(CancelableOperations.setValue(propertyId, values));
    }

    @Override
    public void setTree(NodeId parentId, Tree contentTree) throws RepositoryException {
        addOperation(CancelableOperations.setTree(parentId, contentTree));
    }

    /**
     * Determines the cancellation behavior from the list of {@link ChangeLogImpl#operations operations}
     * and the current operation <code>op</code>:
     * <ul>
     * <li>When the current operation is cancelled by the last operation, the list of operations
     *   is not modified.</li>
     * <li>When the current operation and the last operation cancel each other, the last operation is
     *   removed from the list of operations.</li>
     * <li>When the last operation is cancelled by this operation, the last operation is removed from
     *   the list of operations and determination of cancellation starts from scratch.</li>
     * <li>Otherwise add the current operation to the list of operations.</li>
     * </ul>
     */
    @Override
    public void addOperation(CancelableOperation op) throws RepositoryException {
        CancelableOperation otherOp = op;
        for (OperationsBackwardWithSentinel it = new OperationsBackwardWithSentinel(this); it.hasNext(); ) {
            CancelableOperation thisOp = it.next();
            switch (thisOp.cancel(otherOp)) {
                case CancelableOperation.CANCEL_THIS:
                    it.remove();
                    continue;
                case CancelableOperation.CANCEL_OTHER:
                    return;
                case CancelableOperation.CANCEL_BOTH:
                    it.remove();
                    return;
                case CancelableOperation.CANCEL_NONE:
                    super.addOperation(otherOp);
                    return;
                default:
                    assert false : "Invalid case in switch";
            }
        }
    }

    // -----------------------------------------------------< private >---

    // -----------------------------------------------------< CancelableOperations >---

}
