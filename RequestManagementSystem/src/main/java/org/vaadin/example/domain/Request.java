package org.vaadin.example.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private AppUser client;

    @ManyToOne
    @JoinColumn(name = "operator_id")
    private AppUser operator;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "TEXT")
    private String data;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
