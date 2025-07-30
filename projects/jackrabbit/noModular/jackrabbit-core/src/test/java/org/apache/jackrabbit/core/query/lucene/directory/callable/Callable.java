package org.apache.jackrabbit.core.query.lucene.directory.callable;

import org.apache.jackrabbit.core.query.lucene.directory.DirectoryManager;

public interface Callable {

    public void call(DirectoryManager directoryManager) throws Exception;
}
