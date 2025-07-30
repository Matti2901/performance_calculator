package org.apache.roller.weblogger.ui.core.filters.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.roller.weblogger.WebloggerException;
import org.apache.roller.weblogger.business.user.UserManager;
import org.apache.roller.weblogger.business.weblog.WebloggerFactory;
import org.apache.roller.weblogger.pojos.core.permission.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RoleAssignmentRequestWrapper extends HttpServletRequestWrapper {
    private static Log log = LogFactory.getLog(RoleAssignmentRequestWrapper.class);

    public RoleAssignmentRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public boolean isUserInRole(String roleName) {
        UserManager umgr = WebloggerFactory.getWeblogger().getUserManager();
        if (getUserPrincipal() != null) {
            try {
                User user = umgr.getUserByUserName(getUserPrincipal().getName(), Boolean.TRUE);
                return umgr.hasRole(roleName, user);
            } catch (WebloggerException ex) {
                log.error("ERROR checking user rile", ex);
            }
        }
        return false;
    }
}
