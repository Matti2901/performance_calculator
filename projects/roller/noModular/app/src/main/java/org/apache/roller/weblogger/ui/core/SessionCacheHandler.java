package org.apache.roller.weblogger.ui.core;

import org.apache.roller.weblogger.pojos.core.permission.User;
import org.apache.roller.weblogger.util.cache.CacheHandler;

public class SessionCacheHandler implements CacheHandler {
    private final RollerLoginSessionManager rollerLoginSessionManager;

    public SessionCacheHandler(RollerLoginSessionManager rollerLoginSessionManager) {
        this.rollerLoginSessionManager = rollerLoginSessionManager;
    }

    @Override
    public void invalidate(User user) {
        if (user != null && user.getUserName() != null) {
            rollerLoginSessionManager.sessionCache.remove(user.getUserName());
        }
    }
}
