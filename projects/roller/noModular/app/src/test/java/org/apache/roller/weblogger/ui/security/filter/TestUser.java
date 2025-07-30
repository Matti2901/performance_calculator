package org.apache.roller.weblogger.ui.security.filter;

import org.apache.roller.weblogger.pojos.core.permission.User;

public class TestUser extends User {
    private final String id;

   public TestUser(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
