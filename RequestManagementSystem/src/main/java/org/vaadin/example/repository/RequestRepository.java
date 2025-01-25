package org.vaadin.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.example.domain.Request;
import org.vaadin.example.domain.AppUser;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByClient(AppUser client);
}
