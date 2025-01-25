package org.vaadin.example.service.impl;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vaadin.example.domain.AppUser;
import org.vaadin.example.domain.Role;
import org.vaadin.example.repository.UserRepository;
import org.vaadin.example.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super();
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUser createUser(String name, String rawPassword, Role role) {
        AppUser appUser = AppUser.builder()
                .name(name)
                .role(role)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .build();
        return userRepository.save(appUser);
    }

    @Override
    public Optional<AppUser> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByName(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> appUser = userRepository.findByName(username);
        return new User(
                appUser.get().getName(),
                appUser.get().getPasswordHash(),
                List.of(new SimpleGrantedAuthority(appUser.get().getRole().toString()))
        );
    }
}
