package org.apache.jackrabbit.server.remoting.davex.factory;

import org.apache.jackrabbit.webdav.DavResourceLocator;

/**
 * Locator factory that specially deals with hrefs having a .json extension.
 */
public class DavLocatorFactoryImpl extends org.apache.jackrabbit.webdav.jcr.DavLocatorFactoryImpl {

    public DavLocatorFactoryImpl(String s) {
        super(s);
    }

    @Override
    public DavResourceLocator createResourceLocator(String prefix, String href) {
        return createResourceLocator(prefix, href, false);
    }

    @Override
    public DavResourceLocator createResourceLocator(String prefix, String href, boolean forDestination) {
        DavResourceLocator loc = super.createResourceLocator(prefix, href);
        if (!forDestination && endsWithJson(href)) {
            loc = new WrappingLocator(super.createResourceLocator(prefix, href));
        }
        return loc;
    }

    @Override
    public DavResourceLocator createResourceLocator(String prefix, String workspacePath, String path, boolean isResourcePath) {
        DavResourceLocator loc = super.createResourceLocator(prefix, workspacePath, path, isResourcePath);
        if (isResourcePath && endsWithJson(path)) {
            loc = new WrappingLocator(loc);
        }
        return loc;
    }

    private static boolean endsWithJson(String s) {
        return s.endsWith(".json");
    }
}
