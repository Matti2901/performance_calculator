package org.apache.jackrabbit.core.security.utils;

import org.apache.jackrabbit.core.security.DefaultAccessManager;
import org.apache.jackrabbit.core.security.authorization.WorkspaceAccessManager;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple wrapper around the repository's <code>WorkspaceAccessManager</code>
 * that remembers for which workspaces the access has already been
 * evaluated.
 */
public class WorkspaceAccess {

    private final DefaultAccessManager defaultAccessManager;
    private final WorkspaceAccessManager wspAccessManager;

    private final boolean alwaysAllowed;
    // TODO: entries must be cleared if access permission to wsp changes.
    private final List<String> allowed;
    private final List<String> denied;

    public WorkspaceAccess(DefaultAccessManager defaultAccessManager, WorkspaceAccessManager wspAccessManager,
                           boolean alwaysAllowed) {
        this.defaultAccessManager = defaultAccessManager;
        this.wspAccessManager = wspAccessManager;
        this.alwaysAllowed = alwaysAllowed;
        if (!alwaysAllowed) {
            allowed = new ArrayList<String>(5);
            denied = new ArrayList<String>(5);
        } else {
            allowed = denied = null;
        }
    }

    public boolean canAccess(String workspaceName) throws RepositoryException {
        if (alwaysAllowed || wspAccessManager == null || allowed.contains(workspaceName)) {
            return true;
        } else if (denied.contains(workspaceName)) {
            return false;
        }

        // not yet tested -> ask the workspace-accessmanager.
        boolean canAccess = wspAccessManager.grants(defaultAccessManager.principals, workspaceName);
        if (canAccess) {
            allowed.add(workspaceName);
        } else {
            denied.add(workspaceName);
        }
        return canAccess;
    }
}
