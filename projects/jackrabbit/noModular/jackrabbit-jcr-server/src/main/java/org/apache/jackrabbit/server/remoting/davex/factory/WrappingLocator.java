package org.apache.jackrabbit.server.remoting.davex.factory;

import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;

/**
 * Resource locator that removes trailing .json extensions and depth
 * selector that do not form part of the repository path.
 * As the locator and it's factory do not have access to a JCR session
 * the <code>extraJson</code> flag may be reset later on.
 *
 * @see ResourceFactoryImpl#getItem(org.apache.jackrabbit.webdav.jcr.JcrDavSession, org.apache.jackrabbit.webdav.DavResourceLocator)
 */
public class WrappingLocator implements DavResourceLocator {

    public final DavResourceLocator loc;
    public boolean isJsonRequest = true;
    private int depth = Integer.MIN_VALUE;
    private String repositoryPath;

    WrappingLocator(DavResourceLocator loc) {
        this.loc = loc;
    }

    private void extract() {
        String rp = loc.getRepositoryPath();
        rp = rp.substring(0, rp.lastIndexOf('.'));
        int pos = rp.lastIndexOf('.');
        if (pos > -1) {
            String depthStr = rp.substring(pos + 1);
            try {
                depth = Integer.parseInt(depthStr);
                rp = rp.substring(0, pos);
            } catch (NumberFormatException e) {
                // apparently no depth-info -> ignore
            }
        }
        repositoryPath = rp;
    }

    public int getDepth() {
        if (isJsonRequest) {
            if (repositoryPath == null) {
                extract();
            }
            return depth;
        } else {
            return Integer.MIN_VALUE;
        }
    }

    public String getPrefix() {
        return loc.getPrefix();
    }

    public String getResourcePath() {
        return loc.getResourcePath();
    }

    public String getWorkspacePath() {
        return loc.getWorkspacePath();
    }

    public String getWorkspaceName() {
        return loc.getWorkspaceName();
    }

    public boolean isSameWorkspace(DavResourceLocator davResourceLocator) {
        return loc.isSameWorkspace(davResourceLocator);
    }

    public boolean isSameWorkspace(String string) {
        return loc.isSameWorkspace(string);
    }

    public String getHref(boolean b) {
        return loc.getHref(b);
    }

    public boolean isRootLocation() {
        return loc.isRootLocation();
    }

    public DavLocatorFactory getFactory() {
        return loc.getFactory();
    }

    public String getRepositoryPath() {
        if (isJsonRequest) {
            if (repositoryPath == null) {
                extract();
            }
            return repositoryPath;
        } else {
            return loc.getRepositoryPath();
        }
    }
}
