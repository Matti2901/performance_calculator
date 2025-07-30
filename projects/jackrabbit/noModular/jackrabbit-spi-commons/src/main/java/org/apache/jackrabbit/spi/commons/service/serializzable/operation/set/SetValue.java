package org.apache.jackrabbit.spi.commons.service.serializzable.operation.set;

import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.PropertyId;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.commons.service.serializzable.operation.Operation;

import javax.jcr.RepositoryException;

public class SetValue implements Operation {

    private final PropertyId propertyId;

    private final QValue[] values;

    private final boolean isMultiValued;

    public SetValue(PropertyId propertyId, QValue[] values, boolean isMultiValued) {
        this.propertyId = propertyId;
        this.values = values;
        this.isMultiValued = isMultiValued;
    }

    /**
     * {@inheritDoc}
     */
    public void replay(Batch batch) throws RepositoryException {
        if (isMultiValued) {
            batch.setValue(propertyId, values);
        } else {
            batch.setValue(propertyId, values[0]);
        }
    }
}
