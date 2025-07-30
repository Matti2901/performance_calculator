package org.apache.jackrabbit.core.query.lucene.directory.fs;

import org.apache.jackrabbit.core.query.lucene.directory.FSDirectoryManager;
import org.apache.jackrabbit.core.query.lucene.directory.fs.IndexInputLogWrapper;
import org.apache.lucene.store.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public final class FSDir extends Directory {

    private static final FileFilter FILTER = new FileFilter() {
        public boolean accept(File pathname) {
            return pathname.isFile();
        }
    };

    private final FSDirectory directory;

    public FSDir(File dir, boolean simpleFS) throws IOException {
        if (!dir.mkdirs()) {
            if (!dir.isDirectory()) {
                throw new IOException("Unable to create directory: '" + dir + "'");
            }
        }
        LockFactory lockFactory = new NativeFSLockFactory(dir);
        if (simpleFS) {
            directory = new SimpleFSDirectory(dir, lockFactory);
        } else {
            directory = FSDirectory.open(dir, lockFactory);
        }
    }

    @Override
    public String[] listAll() throws IOException {
        File[] files = directory.getDirectory().listFiles(FILTER);
        if (files == null) {
            return null;
        }
        String[] names = new String[files.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = files[i].getName();
        }
        return names;
    }

    @Override
    public boolean fileExists(String name) throws IOException {
        return directory.fileExists(name);
    }

    @Override
    public long fileModified(String name) throws IOException {
        return directory.fileModified(name);
    }

    @Override
    public void touchFile(String name) throws IOException {
        directory.touchFile(name);
    }

    @Override
    public void deleteFile(String name) throws IOException {
        directory.deleteFile(name);
    }

    @Override
    public long fileLength(String name) throws IOException {
        return directory.fileLength(name);
    }

    @Override
    public IndexOutput createOutput(String name) throws IOException {
        return directory.createOutput(name);
    }

    @Override
    public IndexInput openInput(String name) throws IOException {
        IndexInput in = directory.openInput(name);
        return new IndexInputLogWrapper(name, in);
    }

    @Override
    public void close() throws IOException {
        directory.close();
    }

    @Override
    public IndexInput openInput(String name, int bufferSize)
            throws IOException {
        IndexInput in = directory.openInput(name, bufferSize);
        return new IndexInputLogWrapper(name, in);
    }

    @Override
    public Lock makeLock(String name) {
        return directory.makeLock(name);
    }

    @Override
    public void clearLock(String name) throws IOException {
        directory.clearLock(name);
    }

    @Override
    public void setLockFactory(LockFactory lockFactory) throws IOException {
        directory.setLockFactory(lockFactory);
    }

    @Override
    public LockFactory getLockFactory() {
        return directory.getLockFactory();
    }

    @Override
    public String getLockID() {
        return directory.getLockID();
    }

    public String toString() {
        return getClass().getName() + '@' + directory;
    }
}
