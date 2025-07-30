package org.apache.jackrabbit.core.security.util;

import java.security.Principal;
import java.util.Iterator;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.spi.Name;

public interface UserManagerImplInterface {
	public String getGroupsPath();
	public String getUsersPath();
	public Authorizable getAuthorizable(Principal principal) throws RepositoryException;
	public Iterator<Authorizable> findAuthorizables(String pPrincipalName, String simpleFilter, int searchTypeUser) 
			throws RepositoryException;
	public Name getConstantAuthorizableFolder();
	public Name getP_PRINCIPAL_NAME();
}
