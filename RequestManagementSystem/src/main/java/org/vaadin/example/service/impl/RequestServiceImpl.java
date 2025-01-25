package org.vaadin.example.service.impl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.vaadin.example.broadcast.Broadcaster;
import org.vaadin.example.domain.Request;
import org.vaadin.example.domain.Status;
import org.vaadin.example.domain.AppUser;
import org.vaadin.example.repository.RequestRepository;
import org.vaadin.example.service.RequestService;

import java.util.List;
import java.util.Optional;

@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    public RequestServiceImpl(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public Request createRequest(AppUser client, String data) {
        Request request = Request.builder()
                .client(client)
                .status(Status.NEW)
                .data(data)
                .build();
        Request savedRequest = requestRepository.save(request);
        Broadcaster.broadcast();
        return savedRequest;
    }

    @Override
    public List<Request> findAll() {
        return requestRepository.findAll();
    }

    @Override
    public List<Request> findAllByClient(AppUser client) {
        return requestRepository.findAllByClient(client);
    }

    @Override
    public Optional<Request> findById(Long id) {
        return requestRepository.findById(id);
    }

    @Override
    public Request viewRequest(Request request) {
        return request;
    }

    @Override
    public Request save(Request request) {
        return requestRepository.save(request);
    }

    @Override
    @PreAuthorize("hasAuthority('OPERATOR')")
    public void markError(Request request, String comment, AppUser operator) {
        request.setStatus(Status.ERROR);
        request.setComment(comment);
        request.setOperator(operator);
        requestRepository.save(request);
        Broadcaster.broadcast();
    }

    @Override
    public void markFixed(Request request, String data) {
        request.setStatus(Status.FIXED);
        request.setData(data);
        requestRepository.save(request);
        Broadcaster.broadcast();
    }

    @Override
    @PreAuthorize("hasAuthority('OPERATOR')")
    public void markDone(Request request, AppUser operator) {
        request.setStatus(Status.DONE);
        request.setOperator(operator);
        requestRepository.save(request);
        Broadcaster.broadcast();
    }
}
