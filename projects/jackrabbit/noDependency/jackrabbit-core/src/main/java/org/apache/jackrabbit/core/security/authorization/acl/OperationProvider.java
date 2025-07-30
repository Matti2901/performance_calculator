package org.apache.jackrabbit.core.security.authorization.acl;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.core.NodeImpl;
import org.apache.jackrabbit.core.security.authorization.AccessControlConstants;

public class OperationProvider implements AccessControlConstants{
	static boolean isAccessControlled(NodeImpl node) throws RepositoryException {
        return node.isAccessControllable();
    }
	  static boolean isRepoAccessControlled(NodeImpl node) throws RepositoryException {
	        return node.hasNode(N_REPO_POLICY) && node.isNodeType(NT_REP_REPO_ACCESS_CONTROLLABLE);
	    }
}
