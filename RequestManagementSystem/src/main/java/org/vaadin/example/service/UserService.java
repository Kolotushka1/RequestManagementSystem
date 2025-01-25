package org.vaadin.example.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.vaadin.example.domain.Role;
import org.vaadin.example.domain.AppUser;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    public AppUser createUser(String name, String rawPassword, Role role);
    public Optional<AppUser> findByName(String name);
    public Boolean existsByUsername(String username);
}
