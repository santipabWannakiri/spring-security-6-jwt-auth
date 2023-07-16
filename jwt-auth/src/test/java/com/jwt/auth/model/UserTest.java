package com.jwt.auth.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;
    private Set<Role> roles;

    @BeforeEach
    void testUserConstructor() {
        Privilege readPrivilege = new Privilege(1L,"READ");
        Privilege writePrivilege = new Privilege(2L,"WRITE");
        Privilege deletePrivilege = new Privilege(3L,"DELETE");
        Role superAdminRole =  new Role(1L,"SUPER", Set.of(readPrivilege, writePrivilege, deletePrivilege));
        Set<Role> roles = new HashSet<>();
        roles.add(superAdminRole);
        user = new User(1L, "superAdmin","superAdmin@gmail.com","zxcv", roles,true);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getId() {
        assertEquals(1L, user.getId());
    }

    @Test
    void getUsername() {
        assertEquals("superAdmin",user.getUsername());
    }

    @Test
    void getEmail() {
        assertEquals("superAdmin@gmail.com",user.getEmail());
    }

    @Test
    void getPassword() {
        assertEquals("zxcv",user.getPassword());
    }

    @Test
    void getRoles() {
        for (Role role : user.getRoles()) {
            assertEquals("SUPER", role.getName());
        }
    }

    @Test
    void getIsActive() {
        assertEquals(true, user.getIsActive());
    }

}