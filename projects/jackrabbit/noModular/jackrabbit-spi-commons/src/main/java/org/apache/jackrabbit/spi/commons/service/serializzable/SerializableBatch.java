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
package org.apache.jackrabbit.spi.commons.service.serializzable;

import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Tree;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.Move;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.Operation;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.Remove;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.ReorderNodes;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.add.AddNode;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.add.AddProperty;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.set.SetMixins;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.set.SetPrimaryType;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.set.SetTree;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.set.SetValue;

import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.PathNotFoundException;
import javax.jcr.AccessDeniedException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.lock.LockException;
import javax.jcr.version.VersionException;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * <code>SerializableBatch</code> implements a serializable SPI Batch, which
 * simply records all calls and replays them when asked for. The client of
 * this batch must ensure that the passed {@link QValue} instances are
 * serializable, otherwise the serializing the <code>Batch</code> will fail!
 */
public class SerializableBatch implements Batch, Serializable {

    private List<Operation> recording = new ArrayList<Operation>();

    private final ItemId itemId;

    /**
     * Creates a new <code>SerializableBatch</code>.
     *
     * @param itemId the id of the item where save was called. To indicate that
     *               save was called on the session, the id of the root node
     *               must be passed.
     */
    public SerializableBatch(ItemId itemId) {
        this.itemId = itemId;
    }

    /**
     * @return the item id where save was called for this batch.
     */
    public ItemId getSaveTarget() {
        return itemId;
    }

    /**
     * Replays this batch on the given <code>batch</code>. For a description of
     * the exception see {@link org.apache.jackrabbit.spi.RepositoryService#submit(Batch)}.
     *
     * @param batch the target batch.
     */
    public void replay(Batch batch) throws PathNotFoundException, ItemNotFoundException, NoSuchNodeTypeException, ValueFormatException, VersionException, LockException, ConstraintViolationException, AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
        for (Operation operation : recording) {
            operation.replay(batch);
        }
    }

    //----------------------------< Batch >-------------------------------------

    public void addNode(NodeId parentId,
                        Name nodeName,
                        Name nodetypeName,
                        String uuid) {
        recording.add(new AddNode(parentId, nodeName, nodetypeName, uuid));
    }

    public void addProperty(NodeId parentId, Name propertyName, QValue value) {
        recording.add(new AddProperty(parentId, propertyName,
                new QValue[]{value}, false));
    }

    public void addProperty(NodeId parentId,
                            Name propertyName,
                            QValue[] values) {
        recording.add(new AddProperty(parentId, propertyName, values, true));
    }

    public void setValue(PropertyId propertyId, QValue value) {
        recording.add(new SetValue(propertyId, new QValue[]{value}, false));
    }

    public void setValue(PropertyId propertyId, QValue[] values) {
        recording.add(new SetValue(propertyId, values, true));
    }

    public void remove(ItemId itemId) {
        recording.add(new Remove(itemId));
    }

    public void reorderNodes(NodeId parentId,
                             NodeId srcNodeId,
                             NodeId beforeNodeId) {
        recording.add(new ReorderNodes(parentId, srcNodeId, beforeNodeId));
    }

    public void setMixins(NodeId nodeId, Name[] mixinNodeTypeIds) {
        recording.add(new SetMixins(nodeId, mixinNodeTypeIds));
    }

    public void setPrimaryType(NodeId nodeId, Name primaryNodeTypeName) throws RepositoryException {
        recording.add(new SetPrimaryType(nodeId, primaryNodeTypeName));
    }

    public void move(NodeId srcNodeId,
                     NodeId destParentNodeId,
                     Name destName) {
        recording.add(new Move(srcNodeId, destParentNodeId, destName));
    }

    public void setTree(NodeId parentId, Tree contentTree)
            throws RepositoryException {
        recording.add(new SetTree(parentId, contentTree));
    }
    //----------------------------< internal >----------------------------------

}
