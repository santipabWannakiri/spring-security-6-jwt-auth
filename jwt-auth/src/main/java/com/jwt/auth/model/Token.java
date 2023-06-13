package com.jwt.auth.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tokenValue;
    private TokenType tokenType;
    private TokenStatus tokenStatus;
    private Date expirationDate;
    private Date creationTimestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Token(String tokenValue, TokenType tokenType, TokenStatus tokenStatus, Date expirationDate, Date creationTimestamp, User user) {
        this.tokenValue = tokenValue;
        this.tokenType = tokenType;
        this.tokenStatus = tokenStatus;
        this.expirationDate = expirationDate;
        this.creationTimestamp = creationTimestamp;
        this.user = user;
    }
}
