package org.apache.jackrabbit.core.version.manager.impl;

import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.observation.DelegatingObservationDispatcher;
import org.apache.jackrabbit.core.observation.EventStateCollection;
import org.apache.jackrabbit.core.observation.EventStateCollectionFactory;
import org.apache.jackrabbit.core.version.manager.InternalVersionManagerImpl;

import javax.jcr.RepositoryException;

public final class DynamicESCFactory implements EventStateCollectionFactory {

    /**
     * the observation manager
     */
    private final DelegatingObservationDispatcher obsMgr;

    /**
     * The event source of the current thread.
     */
    private final ThreadLocal<SessionImpl> source =
            new ThreadLocal<SessionImpl>();

    /**
     * Creates a new event state collection factory
     *
     * @param obsMgr dispatcher
     */
    public DynamicESCFactory(DelegatingObservationDispatcher obsMgr) {
        this.obsMgr = obsMgr;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This object uses one instance of a <code>LocalItemStateManager</code>
     * to update data on behalf of many sessions. In order to maintain the
     * association between update operation and session who actually invoked
     * the update, an internal event source is used.
     */
    public EventStateCollection createEventStateCollection()
            throws RepositoryException {
        SessionImpl session = source.get();
        if (session != null) {
            return createEventStateCollection(session);
        } else {
            throw new RepositoryException("Unknown event source.");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This object uses one instance of a <code>LocalItemStateManager</code>
     * to update data on behalf of many sessions. In order to maintain the
     * association between update operation and session who actually invoked
     * the update, an internal event source is used.
     */
    public EventStateCollection createEventStateCollection(SessionImpl source) {
        return obsMgr.createEventStateCollection(source, InternalVersionManagerImpl.SYSTEM_PATH);
    }

    /**
     * Executes the given runnable using the given event source.
     *
     * @param eventSource event source
     * @param runnable    the runnable to execute
     * @return the return value of the executed runnable
     * @throws RepositoryException if an error occurs
     */
    public Object doSourced(SessionImpl eventSource, SourcedTarget runnable)
            throws RepositoryException {
        source.set(eventSource);
        try {
            return runnable.run();
        } finally {
            source.remove();
        }
    }

}
