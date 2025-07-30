package org.apache.jackrabbit.spi.commons.batch.change.consolidating.backward;

import org.apache.jackrabbit.spi.commons.batch.change.consolidating.ConsolidatingChangeLog;
import org.apache.jackrabbit.spi.commons.batch.change.consolidating.cancel.CancelableOperation;
import org.apache.jackrabbit.spi.commons.batch.change.consolidating.cancel.CancelableOperations;

import java.util.Iterator;
import java.util.ListIterator;


public class OperationsBackwardWithSentinel implements Iterator<CancelableOperation> {
    private final ListIterator<CancelableOperation> it ;
    private boolean last;
    private boolean done;
    public OperationsBackwardWithSentinel(ConsolidatingChangeLog consolidatingChangeLog){
        this.it = consolidatingChangeLog.operations.listIterator(consolidatingChangeLog.operations.size());
        this.last = !it.hasPrevious();
    }
    public boolean hasNext() {
        return it.hasPrevious() || last;
    }

    public CancelableOperation next() {
        if (last) {
            done = true;
            return CancelableOperations.empty();
        }
        else {
            CancelableOperation o = it.previous();
            last = !it.hasPrevious();
            return o;
        }
    }

    public void remove() {
        if (done) {
            throw new IllegalStateException("Cannot remove last element");
        }
        else {
            it.remove();
        }
    }
}