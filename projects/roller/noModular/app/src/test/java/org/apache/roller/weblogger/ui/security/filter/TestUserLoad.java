package org.apache.roller.weblogger.ui.security.filter;

import org.apache.roller.weblogger.pojos.core.permission.User;

public class TestUserLoad extends User {
    private final String id;

    public TestUserLoad(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
