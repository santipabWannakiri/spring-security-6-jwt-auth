package com.jwt.auth.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tokenValue;
    private LocalDateTime expirationDate;
    private LocalDateTime creationTimestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
