package org.apache.jackrabbit.spi.commons.batch.change.consolidating.cancel;

import org.apache.jackrabbit.spi.*;
import org.apache.jackrabbit.spi.commons.batch.change.consolidating.ConsolidatingChangeLog;
import org.apache.jackrabbit.spi.commons.batch.operation.Operation;
import org.apache.jackrabbit.spi.commons.batch.operation.Operations;

import javax.jcr.RepositoryException;

/**
 * Factory for creating {@link CancelableOperation CancelableOperation}s.
 * The inner classes of this class all implement the <code>CancelableOperation</code> interface.
 *
 * @see Operation
 */
public final class CancelableOperations {
    private CancelableOperations() {
        super();
    }

    // -----------------------------------------------------< Empty >---

    /**
     * An <code>Empty</code> operation never cancels another operation and is never
     * cancelled by any other operation.
     */
    public static class Empty extends Operations.Empty implements CancelableOperation {

        /**
         * @return {@link CancelableOperation#CANCEL_NONE}
         */
        public int cancel(CancelableOperation other) throws RepositoryException {
            return CANCEL_NONE;
        }
    }

    /**
     * Factory method for creating an {@link Empty Empty} operation.
     *
     * @return
     */
    public static CancelableOperation empty() {
        return new Empty();
    }

    // -----------------------------------------------------< AddNode >---

    /**
     * An <code>AddNode</code> operation is is cancelled by a
     * {@link Remove Remove} operation higher up the tree.
     * The remove operation is also cancelled if it is targeted at the same node than this add
     * operation.
     */
    public static class AddNode extends Operations.AddNode implements CancelableOperation {

        public AddNode(NodeId parentId, Name nodeName, Name nodetypeName, String uuid) {
            super(parentId, nodeName, nodetypeName, uuid);
        }

        /**
         * @return <ul>
         * <li>{@link CancelableOperation#CANCEL_BOTH CANCEL_BOTH} if
         *   <code>other</code> is an instance of
         *   {@link Remove Remove} and has this node
         *   as target.</li>
         * <li>{@link CancelableOperation#CANCEL_THIS CANCEL_THIS} if
         *  <code>other</code> is an instance of
         *  {@link Remove Remove} and has an node higher up
         *  the hierarchy as target.</li>
         * <li>{@link CancelableOperation#CANCEL_NONE CANCEL_NONE} otherwise.</li>
         * </ul>
         */
        public int cancel(CancelableOperation other) throws RepositoryException {
            if (other instanceof Remove) {
                Path thisPath = ConsolidatingChangeLog.getPath(parentId, nodeName);
                Path otherPath = ConsolidatingChangeLog.getPath(((Remove) other).itemId);
                if (thisPath == null || otherPath == null) {
                    return CANCEL_NONE;
                }
                if (thisPath.equals(otherPath)) {
                    return CANCEL_BOTH;
                }
                return (thisPath.isDescendantOf(otherPath))
                        ? CANCEL_THIS
                        : CANCEL_NONE;
            }
            return CANCEL_NONE;
        }
    }

    /**
     * Factory method for creating an {@link AddNode AddNode} operation.
     *
     * @param parentId
     * @param nodeName
     * @param nodetypeName
     * @param uuid
     * @return
     * @see Batch#addNode(NodeId, Name, Name, String)
     */
    public static CancelableOperation addNode(NodeId parentId, Name nodeName, Name nodetypeName, String uuid) {
        return new AddNode(parentId, nodeName, nodetypeName, uuid);
    }

    // ---------------------------------------------------< AddProperty >---

    /**
     * <code>AddProperty</code> operations might cancel with
     * {@link Remove Remove} and
     * {@link SetValue SetValue} operations.
     */
    public static class AddProperty extends Operations.AddProperty implements CancelableOperation {

        public AddProperty(NodeId parentId, Name propertyName, QValue value) {
            super(parentId, propertyName, value);
        }

        public AddProperty(NodeId parentId, Name propertyName, QValue[] values) {
            super(parentId, propertyName, values);
        }

        /**
         * @return <ul>
         * <li>{@link CancelableOperation#CANCEL_BOTH CANCEL_BOTH} if
         *  <code>other</code> is an instance of
         *  {@link Remove Remove} and has this property as
         *  target or if <code>other</code> is an instance of
         *  {@link SetValue SetValue} for a value of
         *  <code>null</code> and has this property as target.</li>
         * <li>{@link CancelableOperation#CANCEL_THIS CANCEL_THIS} if
         *   <code>other</code> is an instance of
         *   {@link Remove Remove} and has a node higher up
         *   the hierarchy as target.</li>
         * <li>{@link CancelableOperation#CANCEL_OTHER CANCEL_OTHER} if
         *   <code>other</code> is an instance of
         *   {@link SetValue SetValue} and has this
         *   property as target.</li>
         * <li>{@link CancelableOperation#CANCEL_NONE CANCEL_NONE} otherwise.</li>
         * </ul>
         */
        public int cancel(CancelableOperation other) throws RepositoryException {
            if (other instanceof Remove) {
                Path thisPath = ConsolidatingChangeLog.getPath(parentId, propertyName);
                Path otherPath = ConsolidatingChangeLog.getPath(((Remove) other).itemId);
                if (thisPath == null || otherPath == null) {
                    return CANCEL_NONE;
                }
                if (thisPath.equals(otherPath)) {
                    return CANCEL_BOTH;
                }
                return (thisPath.isDescendantOf(otherPath))
                        ? CANCEL_THIS
                        : CANCEL_NONE;
            }
            if (other instanceof SetValue) {
                SetValue setValue = (SetValue) other;
                Path thisPath = ConsolidatingChangeLog.getPath(parentId, propertyName);
                Path otherPath = ConsolidatingChangeLog.getPath(setValue.propertyId);
                if (thisPath == null || otherPath == null) {
                    return CANCEL_NONE;
                }
                if (thisPath.equals(otherPath)) {
                    if (!isMultivalued && setValue.values[0] == null) {
                        return CANCEL_BOTH;
                    } else if (values.length == setValue.values.length) {
                        for (int k = 0; k < values.length; k++) {
                            if (!values[k].equals(setValue.values[k])) {
                                return CANCEL_NONE;
                            }
                        }
                        return CANCEL_OTHER;
                    }
                }
            }
            return CANCEL_NONE;
        }
    }

    /**
     * Factory method for creating an {@link AddProperty AddProperty} operation.
     *
     * @param parentId
     * @param propertyName
     * @param value
     * @return
     * @see Batch#addProperty(NodeId, Name, QValue)
     */
    public static CancelableOperation addProperty(NodeId parentId, Name propertyName, QValue value) {
        return new AddProperty(parentId, propertyName, value);
    }

    /**
     * Factory method for creating an {@link AddProperty AddProperty} operation.
     *
     * @param parentId
     * @param propertyName
     * @param values
     * @return
     * @see Batch#addProperty(NodeId, Name, QValue[])
     */
    public static CancelableOperation addProperty(NodeId parentId, Name propertyName, QValue[] values) {
        return new AddProperty(parentId, propertyName, values);
    }

    // ----------------------------------------------------------< Move >---

    /**
     * An <code>Move</code> operation never cancels another operation and is never
     * cancelled by any other operation.
     */
    public static class Move extends Operations.Move implements CancelableOperation {

        public Move(NodeId srcNodeId, NodeId destParentNodeId, Name destName) {
            super(srcNodeId, destParentNodeId, destName);
        }

        /**
         * @return {@link CancelableOperation#CANCEL_NONE CANCEL_NONE}
         */
        public int cancel(CancelableOperation other) {
            return CANCEL_NONE;
        }
    }

    /**
     * Factory method for creating a {@link Move Move} operation.
     *
     * @param srcNodeId
     * @param destParentNodeId
     * @param destName
     * @return
     * @see Batch#move(NodeId, NodeId, Name)
     */
    public static CancelableOperation move(NodeId srcNodeId, NodeId destParentNodeId, Name destName) {
        return new Move(srcNodeId, destParentNodeId, destName);
    }

    // --------------------------------------------------------< Remove >---

    /**
     * An <code>Remove</code> operation never cancels another operation and is never
     * cancelled by any other operation.
     */
    public static class Remove extends Operations.Remove implements CancelableOperation {

        public Remove(ItemId itemId) {
            super(itemId);
        }

        /**
         * @return {@link CancelableOperation#CANCEL_NONE CANCEL_NONE}
         */
        public int cancel(CancelableOperation other) {
            return CANCEL_NONE;
        }
    }

    /**
     * Factory method for creating a {@link Remove Remove} operation.
     *
     * @param itemId
     * @return
     * @see Batch#move(NodeId, NodeId, Name)
     */
    public static CancelableOperation remove(ItemId itemId) {
        return new Remove(itemId);
    }

    // -------------------------------------------------< Reorder Nodes >---

    /**
     * A <code>ReorderNodes</code> operation might cancel with
     * {@link Remove Remove} and
     * {@link ReorderNodes ReorderNodes} operations.
     */
    public static class ReorderNodes extends Operations.ReorderNodes implements CancelableOperation {

        public ReorderNodes(NodeId parentId, NodeId srcNodeId, NodeId beforeNodeId) {
            super(parentId, srcNodeId, beforeNodeId);
        }

        /**
         * @return <ul>
         * <li>{@link CancelableOperation#CANCEL_THIS CANCEL_THIS} if
         *   <code>other</code> is an instance of
         *   {@link Remove Remove} and has an node higher up
         *   the hierarchy or this node as target. Or if <code>other</code> is an instance of
         *   {@link ReorderNodes ReorderNodes} which
         *   has this node as target and neither <code>srcNodeId</code> nor <code>beforeNodeId</code>
         *   has same name siblings.</li>
         * <li>{@link CancelableOperation#CANCEL_NONE CANCEL_NONE} otherwise.</li>
         * </ul>
         */
        public int cancel(CancelableOperation other) throws RepositoryException {
            if (other instanceof Remove) {
                Path thisPath = ConsolidatingChangeLog.getPath(srcNodeId);
                Path otherPath = ConsolidatingChangeLog.getPath(((Remove) other).itemId);
                if (thisPath == null || otherPath == null) {
                    return CANCEL_NONE;
                }
                return thisPath.isDescendantOf(otherPath) || thisPath.equals(otherPath)
                        ? CANCEL_THIS
                        : CANCEL_NONE;
            }
            if (other instanceof ReorderNodes) {
                Path thisPath = ConsolidatingChangeLog.getPath(parentId);
                Path otherPath = ConsolidatingChangeLog.getPath(((ReorderNodes) other).parentId);
                if (thisPath == null || otherPath == null) {
                    return CANCEL_NONE;
                }
                return thisPath.equals(otherPath) && !hasSNS(srcNodeId) && !hasSNS(beforeNodeId)
                        ? CANCEL_THIS
                        : CANCEL_NONE;
            }
            return CANCEL_NONE;
        }

        private boolean hasSNS(NodeId nodeId) {
            if (nodeId != null) {
                Path path = ConsolidatingChangeLog.getPath(nodeId);
                return path != null && path.getIndex() > 1;
            }

            return false;
        }
    }

    /**
     * Factory method for creating a {@link ReorderNodes ReorderNodes} operation.
     *
     * @param parentId
     * @param srcNodeId
     * @param beforeNodeId
     * @return
     * @see Batch#reorderNodes(NodeId, NodeId, NodeId)
     */
    public static CancelableOperation reorderNodes(NodeId parentId, NodeId srcNodeId, NodeId beforeNodeId) {
        return new ReorderNodes(parentId, srcNodeId, beforeNodeId);
    }

    // -----------------------------------------------------< SetMixins >---

    /**
     * A <code>SetMixins</code> operation might cancel with
     * {@link Remove Remove} and
     * {@link SetMixins SetMixins} operations.
     */
    public static class SetMixins extends Operations.SetMixins implements CancelableOperation {

        public SetMixins(NodeId nodeId, Name[] mixinNodeTypeNames) {
            super(nodeId, mixinNodeTypeNames);
        }

        /**
         * @return <ul>
         * <li>{@link CancelableOperation#CANCEL_THIS CANCEL_THIS} if
         *   <code>other</code> is an instance of
         *   {@link Remove Remove} and has an node higher up
         *   the hierarchy or this node as target. Or if <code>other</code> is an instance of
         *   {@link SetMixins SetMixins} which has this node
         *   as target and has the same <code>mixinNodeTypeNames</code>.</li>
         * <li>{@link CancelableOperation#CANCEL_NONE CANCEL_NONE} otherwise.</li>
         * </ul>
         */
        public int cancel(CancelableOperation other) throws RepositoryException {
            if (other instanceof Remove) {
                Path thisPath = ConsolidatingChangeLog.getPath(nodeId);
                Path otherPath = ConsolidatingChangeLog.getPath(((Remove) other).itemId);
                if (thisPath == null || otherPath == null) {
                    return CANCEL_NONE;
                }
                return thisPath.isDescendantOf(otherPath) || thisPath.equals(otherPath)
                        ? CANCEL_THIS
                        : CANCEL_NONE;
            }
            if (other instanceof SetMixins) {
                SetMixins setMixin = (SetMixins) other;
                if (mixinNodeTypeNames.length == setMixin.mixinNodeTypeNames.length) {
                    Path thisPath = ConsolidatingChangeLog.getPath(nodeId);
                    Path otherPath = ConsolidatingChangeLog.getPath(setMixin.nodeId);
                    if (thisPath == null || otherPath == null) {
                        return CANCEL_NONE;
                    }
                    if (thisPath.equals(otherPath)) {
                        for (int k = 0; k < mixinNodeTypeNames.length; k++) {
                            if (!mixinNodeTypeNames[k].equals(setMixin.mixinNodeTypeNames[k])) {
                                return CANCEL_NONE;
                            }
                        }
                        return CANCEL_THIS;
                    }
                }
            }
            return CANCEL_NONE;
        }
    }

    /**
     * Factory method for creating a {@link SetMixins} operation.
     *
     * @param nodeId
     * @param mixinNodeTypeNames
     * @return
     * @see Batch#setMixins(NodeId, Name[])
     */
    public static CancelableOperation setMixins(NodeId nodeId, Name[] mixinNodeTypeNames) {
        return new SetMixins(nodeId, mixinNodeTypeNames);
    }

    // -----------------------------------------------------< SetMixins >---

    /**
     * A <code>SetPrimaryType</code> operation might cancel with
     * {@link Remove Remove} and
     * {@link SetPrimaryType SetPrimaryType} operations.
     */
    public static class SetPrimaryType extends Operations.SetPrimaryType implements CancelableOperation {

        public SetPrimaryType(NodeId nodeId, Name primaryTypeName) {
            super(nodeId, primaryTypeName);
        }

        /**
         * @return <ul>
         * <li>{@link CancelableOperation#CANCEL_THIS CANCEL_THIS} if
         *   <code>other</code> is an instance of
         *   {@link Remove Remove} and has an node higher up
         *   the hierarchy or this node as target. Or if <code>other</code> is an instance of
         *   {@link SetMixins SetMixins} which has this node
         *   as target and has the same <code>mixinNodeTypeNames</code>.</li>
         * <li>{@link CancelableOperation#CANCEL_NONE CANCEL_NONE} otherwise.</li>
         * </ul>
         */
        public int cancel(CancelableOperation other) throws RepositoryException {
            if (other instanceof Remove) {
                Path thisPath = ConsolidatingChangeLog.getPath(nodeId);
                Path otherPath = ConsolidatingChangeLog.getPath(((Remove) other).itemId);
                if (thisPath == null || otherPath == null) {
                    return CANCEL_NONE;
                }
                return thisPath.isDescendantOf(otherPath) || thisPath.equals(otherPath)
                        ? CANCEL_THIS
                        : CANCEL_NONE;
            }
            if (other instanceof SetPrimaryType) {
                SetPrimaryType setPrimaryType = (SetPrimaryType) other;
                if (primaryTypeName.equals(setPrimaryType.primaryTypeName)) {
                    Path thisPath = ConsolidatingChangeLog.getPath(nodeId);
                    Path otherPath = ConsolidatingChangeLog.getPath(setPrimaryType.nodeId);
                    if (thisPath == null || otherPath == null) {
                        return CANCEL_NONE;
                    }
                    if (thisPath.equals(otherPath)) {
                        return CANCEL_THIS;
                    }
                }
            }
            return CANCEL_NONE;
        }
    }

    /**
     * Factory method for creating a {@link SetPrimaryType} operation.
     *
     * @param nodeId
     * @param primaryTypeName
     * @return
     * @see Batch#setPrimaryType(NodeId, Name)
     */
    public static CancelableOperation setPrimaryType(NodeId nodeId, Name primaryTypeName) {
        return new SetPrimaryType(nodeId, primaryTypeName);
    }

    // ------------------------------------------------------< SetValue >---

    /**
     * A <code>SetValue</code> operation might cancel with
     * {@link Remove Remove} and
     * {@link SetValue SetValue} operations.
     */
    public static class SetValue extends Operations.SetValue implements CancelableOperation {
        public SetValue(PropertyId propertyId, QValue value) {
            super(propertyId, value);
        }

        public SetValue(PropertyId propertyId, QValue[] values) {
            super(propertyId, values);
        }

        /**
         * @return <ul>
         * <li>{@link CancelableOperation#CANCEL_THIS CANCEL_THIS} if
         *   <code>other</code> is an instance of
         *   {@link Remove Remove} and has an node higher up
         *   the hierarchy or this node as target. Or if <code>other</code> is an instance of
         *   {@link SetValue SetValue} which has this
         *   property as target</li>
         * <li>{@link CancelableOperation#CANCEL_NONE CANCEL_NONE} otherwise.</li>
         * </ul>
         */
        public int cancel(CancelableOperation other) throws RepositoryException {
            if (other instanceof Remove) {
                Path thisPath = ConsolidatingChangeLog.getPath(propertyId);
                Path otherPath = ConsolidatingChangeLog.getPath(((Remove) other).itemId);
                if (thisPath == null || otherPath == null) {
                    return CANCEL_NONE;
                }
                return thisPath.isDescendantOf(otherPath) || thisPath.equals(otherPath)
                        ? CANCEL_THIS
                        : CANCEL_NONE;
            }
            if (other instanceof SetValue) {
                Path thisPath = ConsolidatingChangeLog.getPath(propertyId);
                Path otherPath = ConsolidatingChangeLog.getPath(((SetValue) other).propertyId);
                if (thisPath == null || otherPath == null) {
                    return CANCEL_NONE;
                }
                if (thisPath.equals(otherPath)) {
                    return CANCEL_THIS;
                }
            }
            return CANCEL_NONE;
        }
    }

    /**
     * Factory method for creating a {@link SetValue SetValue} operation.
     *
     * @param propertyId
     * @param value
     * @return
     * @see Batch#setValue(PropertyId, QValue)
     */
    public static CancelableOperation setValue(PropertyId propertyId, QValue value) {
        return new SetValue(propertyId, value);
    }

    /**
     * Factory method for creating a {@link SetValue SetValue} operation.
     *
     * @param propertyId
     * @param values
     * @return
     * @see Batch#setValue(PropertyId, QValue[])
     */
    public static CancelableOperation setValue(PropertyId propertyId, QValue[] values) {
        return new SetValue(propertyId, values);
    }


    //--------------------------------------------------------< SetTree >---
    public static class SetTree extends Operations.SetTree implements CancelableOperation {

        public SetTree(NodeId parentId, Tree contentTree) {
            super(parentId, contentTree);
        }

        /**
         * The cancellation only considers canceling the parent node, which corresponds
         * to the policy node.
         */
        public int cancel(CancelableOperation other) throws RepositoryException {
            if (other instanceof Remove) {
                Path thisPath = ConsolidatingChangeLog.getPath(parentId, tree.getName());
                Path otherPath = ConsolidatingChangeLog.getPath(((Remove) other).itemId);
                if (thisPath == null || otherPath == null) {
                    return CANCEL_NONE;
                }
                if (thisPath.equals(otherPath)) {
                    return CANCEL_BOTH;
                }
                return (thisPath.isDescendantOf(otherPath))
                        ? CANCEL_THIS
                        : CANCEL_NONE;
            }
            return CANCEL_NONE;
        }
    }

    /**
     * Factory method for creating an {@link SetTree} operation.
     *
     * @param parentId
     * @param tree
     * @return
     * @see Batch#setTree(NodeId, Tree)
     */
    public static CancelableOperation setTree(NodeId parentId, Tree tree) {
        return new SetTree(parentId, tree);
    }
}
