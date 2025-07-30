package org.apache.jackrabbit.core.version.manager.impl;

import javax.jcr.RepositoryException;

public abstract class SourcedTarget {
    public abstract Object run() throws RepositoryException;
}
