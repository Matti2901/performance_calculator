package org.apache.jackrabbit.core.security.principal;

import org.apache.jackrabbit.api.security.principal.PrincipalIterator;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.core.security.simple.SimpleSecurityManager;

import javax.jcr.Session;
import java.security.Principal;
import java.util.*;

/**
 * Simple Principal provider
 */
public class SimplePrincipalProvider implements PrincipalProvider {

    private final SimpleSecurityManager simpleSecurityManager;
    private final Map<String, Principal> principals = new HashMap<String, Principal>();

    public SimplePrincipalProvider(SimpleSecurityManager simpleSecurityManager) {
        this.simpleSecurityManager = simpleSecurityManager;
        if (simpleSecurityManager.adminID != null) {
            principals.put(simpleSecurityManager.adminID, new AdminPrincipal(simpleSecurityManager.adminID));
        }
        if (simpleSecurityManager.anonymID != null) {
            principals.put(simpleSecurityManager.anonymID, new AnonymousPrincipal());
        }

        EveryonePrincipal everyone = EveryonePrincipal.getInstance();
        principals.put(everyone.getName(), everyone);
    }

    public Principal getPrincipal(String principalName) {
        if (principals.containsKey(principalName)) {
            return principals.get(principalName);
        } else {
            return new UserPrincipal(principalName);
        }
    }

    public PrincipalIterator findPrincipals(String simpleFilter) {
        return findPrincipals(simpleFilter, PrincipalManager.SEARCH_TYPE_ALL);
    }

    public PrincipalIterator findPrincipals(String simpleFilter, int searchType) {
        Principal p = getPrincipal(simpleFilter);
        if (p == null) {
            return PrincipalIteratorAdapter.EMPTY;
        } else if (GroupPrincipals.isGroup(p) && searchType == PrincipalManager.SEARCH_TYPE_NOT_GROUP ||
                !GroupPrincipals.isGroup(p) && searchType == PrincipalManager.SEARCH_TYPE_GROUP) {
            return PrincipalIteratorAdapter.EMPTY;
        } else {
            return new PrincipalIteratorAdapter(Collections.singletonList(p));
        }
    }

    public PrincipalIterator getPrincipals(int searchType) {
        PrincipalIterator it;
        switch (searchType) {
            case PrincipalManager.SEARCH_TYPE_GROUP:
                it = new PrincipalIteratorAdapter(Collections.singletonList(EveryonePrincipal.getInstance()));
                break;
            case PrincipalManager.SEARCH_TYPE_NOT_GROUP:
                Set<Principal> set = new HashSet<Principal>(principals.values());
                set.remove(EveryonePrincipal.getInstance());
                it = new PrincipalIteratorAdapter(set);
                break;
            case PrincipalManager.SEARCH_TYPE_ALL:
                it = new PrincipalIteratorAdapter(principals.values());
                break;
            // no default
            default:
                throw new IllegalArgumentException("Unknown search type " + searchType);
        }
        return it;
    }

    public PrincipalIterator getGroupMembership(Principal principal) {
        if (principal instanceof EveryonePrincipal) {
            return PrincipalIteratorAdapter.EMPTY;
        } else {
            return new PrincipalIteratorAdapter(Collections.singletonList(EveryonePrincipal.getInstance()));
        }
    }

    public void init(Properties options) {
        // nothing to do
    }

    public void close() {
        // nothing to do
    }

    public boolean canReadPrincipal(Session session, Principal principal) {
        return true;
    }
}
