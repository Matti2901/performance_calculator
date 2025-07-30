package org.apache.jackrabbit.core.security.util;

import javax.jcr.Session;

import org.apache.jackrabbit.core.SessionImpl;

public class SecuritySessionImplManager {
	public boolean checkSession(Session systemSession) {
		return systemSession instanceof SessionImpl;
		
	}
	public SessionImpl getSessionImpl(Session session) {
		SessionImpl sImpl = (SessionImpl) session;
		return sImpl;
	}
}
