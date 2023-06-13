package com.jwt.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "is mandatory")
    @Size(min = 4, max = 20, message = "Username must be between 5 and 20 characters")
    private String username;

    @Email
    private String email;

    @NotBlank(message = "is mandatory")
    @Size(min = 4, message = "Username must be between 5 and 20 characters")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    private Boolean isActive;
}