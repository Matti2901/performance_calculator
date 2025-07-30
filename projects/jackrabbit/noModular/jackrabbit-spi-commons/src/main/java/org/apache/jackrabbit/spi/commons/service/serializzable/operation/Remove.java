package org.apache.jackrabbit.spi.commons.service.serializzable.operation;

import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.ItemId;

import javax.jcr.RepositoryException;

public class Remove implements Operation {

    private final ItemId itemId;

    public Remove(ItemId itemId) {
        this.itemId = itemId;
    }

    /**
     * {@inheritDoc}
     */
    public void replay(Batch batch) throws RepositoryException {
        batch.remove(itemId);
    }
}
