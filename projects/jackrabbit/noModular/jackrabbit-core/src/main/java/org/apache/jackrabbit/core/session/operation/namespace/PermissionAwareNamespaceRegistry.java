package org.apache.jackrabbit.core.session.operation.namespace;

import org.apache.jackrabbit.core.security.authorization.Permission;
import org.apache.jackrabbit.core.session.operation.SessionContext;

import javax.jcr.*;

/**
 * Permission aware namespace registry implementation that makes sure that
 * modifications of the namespace registry are only allowed if the editing
 * session has the corresponding permissions.
 */
public class PermissionAwareNamespaceRegistry implements NamespaceRegistry {

    private final SessionContext sessionContext;
    private final NamespaceRegistry nsRegistry;

    public PermissionAwareNamespaceRegistry(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
        this.nsRegistry = sessionContext.getRepositoryContext().getNamespaceRegistry();
    }

    public void registerNamespace(String prefix, String uri) throws NamespaceException, UnsupportedRepositoryOperationException, AccessDeniedException, RepositoryException {
        sessionContext.session.getAccessManager().checkRepositoryPermission(Permission.NAMESPACE_MNGMT);
        nsRegistry.registerNamespace(prefix, uri);
    }

    public void unregisterNamespace(String prefix) throws NamespaceException, UnsupportedRepositoryOperationException, AccessDeniedException, RepositoryException {
        sessionContext.session.getAccessManager().checkRepositoryPermission(Permission.NAMESPACE_MNGMT);
        nsRegistry.unregisterNamespace(prefix);
    }

    public String[] getPrefixes() throws RepositoryException {
        return nsRegistry.getPrefixes();
    }

    public String[] getURIs() throws RepositoryException {
        return nsRegistry.getURIs();
    }

    public String getURI(String prefix) throws NamespaceException, RepositoryException {
        return nsRegistry.getURI(prefix);
    }

    public String getPrefix(String uri) throws NamespaceException, RepositoryException {
        return nsRegistry.getPrefix(uri);
    }
}
