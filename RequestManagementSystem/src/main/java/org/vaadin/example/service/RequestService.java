package org.vaadin.example.service;

import org.vaadin.example.domain.AppUser;
import org.vaadin.example.domain.Request;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    public Request createRequest(AppUser client, String data);
    public List<Request> findAll();
    public List<Request> findAllByClient(AppUser client);
    public Optional<Request> findById(Long id);
    public Request viewRequest(Request request);
    public Request save(Request request);
    public void markError(Request request, String comment, AppUser operator);
    public void markFixed(Request request, String data);
    public void markDone(Request request, AppUser operator);
}
