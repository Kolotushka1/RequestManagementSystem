package org.vaadin.example.security.impl;

import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vaadin.example.domain.AppUser;
import org.vaadin.example.domain.Role;
import org.vaadin.example.security.SecurityService;
import org.vaadin.example.service.UserService;

import java.util.Optional;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public SecurityServiceImpl(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Optional<Boolean> authenticate(String username, String password) {
        return userService.findByName(username)
                .filter(u -> passwordEncoder.matches(password, u.getPasswordHash()))
                .map(u -> {
                    VaadinSession.getCurrent().setAttribute(AppUser.class, u);
                    return true;
                });
    }

    @Override
    public void logout() {
        VaadinSession.getCurrent().close();
    }

    @Override
    public AppUser getAuthenticatedUser() {
        return VaadinSession.getCurrent().getAttribute(AppUser.class);
    }

    @Override
    public boolean isLoggedIn() {
        return getAuthenticatedUser() != null;
    }

    @Override
    public boolean hasRole(Role role) {
        AppUser appUser = getAuthenticatedUser();
        return appUser != null && appUser.getRole() == role;
    }
}
