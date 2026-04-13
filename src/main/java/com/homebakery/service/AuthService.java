package com.homebakery.service;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory demo authentication. Pre-seeded accounts match the login screen; sign-up adds more users.
 */
public final class AuthService {
    private final Map<String, String> usernameToPassword = new ConcurrentHashMap<>();
    private final Map<String, String> usernameToRole = new ConcurrentHashMap<>();

    public AuthService() {
        registerInternal("admin", "admin123", "Manager");
        registerInternal("baker", "baker123", "Baker");
        registerInternal("manager", "manager123", "Manager");
    }

    private void registerInternal(String username, String password, String role) {
        usernameToPassword.put(username.toLowerCase(Locale.ROOT), password);
        usernameToRole.put(username.toLowerCase(Locale.ROOT), role);
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        String key = username.trim().toLowerCase(Locale.ROOT);
        String expected = usernameToPassword.get(key);
        return expected != null && expected.equals(password);
    }

    public String roleFor(String username) {
        if (username == null) {
            return "Baker";
        }
        return usernameToRole.getOrDefault(username.trim().toLowerCase(Locale.ROOT), "Baker");
    }

    /**
     * @return error message, or null if registration succeeded
     */
    public String register(String username, String password, String confirmPassword, String role) {
        Objects.requireNonNull(role);
        if (username == null || username.isBlank()) {
            return "Username is required.";
        }
        if (password == null || password.isEmpty()) {
            return "Password is required.";
        }
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }
        String key = username.trim().toLowerCase(Locale.ROOT);
        if (usernameToPassword.containsKey(key)) {
            return "That username is already taken.";
        }
        usernameToPassword.put(key, password);
        usernameToRole.put(key, role);
        return null;
    }
}
