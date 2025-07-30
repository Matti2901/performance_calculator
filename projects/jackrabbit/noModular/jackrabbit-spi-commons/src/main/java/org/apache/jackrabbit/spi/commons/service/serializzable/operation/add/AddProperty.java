package org.apache.jackrabbit.spi.commons.service.serializzable.operation.add;

import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.Operation;

import javax.jcr.RepositoryException;

public class AddProperty implements Operation {

    private final NodeId parentId;

    private final Name propertyName;

    private final QValue[] values;

    private final boolean isMultiValued;

    public AddProperty(NodeId parentId, Name propertyName,
                       QValue[] values, boolean isMultiValued) {
        this.parentId = parentId;
        this.propertyName = propertyName;
        this.values = values;
        this.isMultiValued = isMultiValued;
    }

    /**
     * {@inheritDoc}
     */
    public void replay(Batch batch) throws RepositoryException {
        if (isMultiValued) {
            batch.addProperty(parentId, propertyName, values);
        } else {
            batch.addProperty(parentId, propertyName, values[0]);
        }
    }
}
