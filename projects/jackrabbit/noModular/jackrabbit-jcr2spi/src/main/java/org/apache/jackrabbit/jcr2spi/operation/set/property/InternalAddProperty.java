package org.apache.jackrabbit.jcr2spi.operation.set.property;

import org.apache.jackrabbit.jcr2spi.operation.IgnoreOperation;
import org.apache.jackrabbit.jcr2spi.operation.creation.AddProperty;
import org.apache.jackrabbit.jcr2spi.state.ItemStateValidator;
import org.apache.jackrabbit.jcr2spi.state.NodeState;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.QValue;

import javax.jcr.RepositoryException;

/**
 * Inner class for adding a protected property.
 */
public final class InternalAddProperty extends AddProperty implements IgnoreOperation {
    private final static int ADD_PROPERTY_OPTIONS = ItemStateValidator.CHECK_ACCESS |
            ItemStateValidator.CHECK_LOCK |
            ItemStateValidator.CHECK_COLLISION |
            ItemStateValidator.CHECK_VERSIONING;

    public InternalAddProperty(NodeState parentState, Name propName, int propertyType, QValue[] values, QPropertyDefinition definition) throws RepositoryException {
        super(parentState, propName, propertyType, values, definition, ADD_PROPERTY_OPTIONS);
    }
}
