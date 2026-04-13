package com.homebakery.ui;

/** Mutable session shown in the main shell sidebar (updated after login). */
public final class SessionContext {
    private volatile String username = "";
    private volatile String roleDisplay = "";

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? "" : username;
    }

    public String getRoleDisplay() {
        return roleDisplay;
    }

    public void setRoleDisplay(String roleDisplay) {
        this.roleDisplay = roleDisplay == null ? "" : roleDisplay;
    }
}
