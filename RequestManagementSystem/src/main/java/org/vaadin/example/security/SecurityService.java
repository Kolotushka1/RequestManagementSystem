package org.vaadin.example.security;

import org.vaadin.example.domain.AppUser;
import org.vaadin.example.domain.Role;

import java.util.Optional;

public interface SecurityService {
    public Optional<Boolean> authenticate(String username, String password);
    public void logout();
    public AppUser getAuthenticatedUser();
    public boolean isLoggedIn();
    public boolean hasRole(Role role);
}
