package org.apache.jackrabbit.spi.commons.batch.change.consolidating.cancel;

import org.apache.jackrabbit.spi.commons.batch.operation.Operation;

import javax.jcr.RepositoryException;

/**
 * This class represent an {@link Operation} which can be cancelled by another operation
 * or which cancels another operation.
 */
public interface CancelableOperation extends Operation {

    /**
     * The other operation cancels this operations
     */
    public static final int CANCEL_THIS = 0;

    /**
     * This operation cancels the other operation
     */
    public static final int CANCEL_OTHER = 1;

    /**
     * This operation and the other operation cancel each other mutually
     */
    public static final int CANCEL_BOTH = 2;

    /**
     * No cancellation
     */
    public static final int CANCEL_NONE = 3;

    /**
     * Determines the cancellation behavior of the <code>other</code> operation
     * on this operation.
     *
     * @param other
     * @return Either {@link #CANCEL_THIS}, {@link #CANCEL_OTHER}, {@link #CANCEL_OTHER}
     * or {@link #CANCEL_NONE}
     * @throws RepositoryException
     */
    public int cancel(CancelableOperation other) throws RepositoryException;
}
