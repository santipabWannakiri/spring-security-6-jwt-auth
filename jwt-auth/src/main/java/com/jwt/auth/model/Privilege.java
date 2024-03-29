package com.jwt.auth.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "privileges")
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public Privilege() {
    }

    public Privilege(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
