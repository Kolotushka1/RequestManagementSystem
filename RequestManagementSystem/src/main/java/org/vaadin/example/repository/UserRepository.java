package org.vaadin.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.example.domain.AppUser;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Boolean existsByName(String name);
    Optional<AppUser> findByName(String name);
}
