package com.jwt.auth.service;

import com.jwt.auth.model.Privilege;
import com.jwt.auth.model.Role;
import com.jwt.auth.model.User;
import com.jwt.auth.repository.RoleRepository;
import com.jwt.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    private Privilege readPrivilege;
    private Privilege writePrivilege;
    private Privilege deletePrivilege;
    private Role superAdminRole;
    private Role adminRole;
    private Role userRole;

    @BeforeEach
    void setUpTestData() {
        readPrivilege = new Privilege(1L, "READ");
        writePrivilege = new Privilege(2L, "WRITE");
        deletePrivilege = new Privilege(3L, "DELETE");
        superAdminRole = new Role(1L, "SUPER", Set.of(readPrivilege, writePrivilege, deletePrivilege));
        adminRole = new Role(2L, "ADMIN", Set.of(readPrivilege, writePrivilege));
        userRole = new Role(3L, "USER", Set.of(readPrivilege));
    }

    @Test
    void findByUser() {
        Set<Role> roles = new HashSet<>();
        roles.add(superAdminRole);
        User expectedUser = new User(1L, "superAdmin", "superAdmin@gmail.com", "zxcv", roles, true);
        String queryUsername = "superAdmin";
        //Set up the mock behavior
        when(userRepository.findByUsername(queryUsername)).thenReturn(expectedUser);
        User resultUser = userRepository.findByUsername(queryUsername);
        assertEquals(resultUser, expectedUser);
    }

    @Test
    void findById() {
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        User expectedUser = new User(1L, "ADMIN", "admin@gmail.com", "zxcv", roles, true);
        Long id = 1L;
        //Set up the mock behavior
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(expectedUser));
        Optional<User> resultUser = userRepository.findById(id);
        assertEquals(resultUser.get(), expectedUser);
    }

    @Test
    void createUser() {
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        User createUser = new User(1L, "USER", "user1@gmail.com", "zxcv", roles, true);
        String queryRole = "USER";

        // Mocking the behavior
        when(roleRepository.findByName(queryRole)).thenReturn(userRole);
        when(userRepository.save(createUser)).thenReturn(createUser);

        //Call
        Role userRole1 = roleRepository.findByName(queryRole);
        User resultUser = userRepository.save(createUser);

        // Verifying that roleRepository was called once
        //If the method is called more than once or not called at all, the test will fail.
        verify(roleRepository, times(1)).findByName(queryRole);
        verify(userRepository, times(1)).save(createUser);

        // Assertions
        assertEquals("USER", userRole1.getName());
        assertEquals("USER", resultUser.getRoles().iterator().next().getName());
        assertEquals(true, resultUser.getIsActive());
    }
}