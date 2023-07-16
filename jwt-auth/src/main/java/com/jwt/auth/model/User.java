package com.jwt.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "users")
@Schema(name = "Signup")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
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
    @JsonIgnore
    private Set<Role> roles;

    @JsonIgnore
    private Boolean isActive;

    public User() {
    }

    public User(Long id, String username, String email, String password, Set<Role> roles, Boolean isActive) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.isActive = isActive;
    }


}