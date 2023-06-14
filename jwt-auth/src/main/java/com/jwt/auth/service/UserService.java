package com.jwt.auth.service;

import com.jwt.auth.model.User;

import java.util.Optional;

public interface UserService {
    public User findByUser(String userName);

    public Optional<User> findById(Long id);

    public User createUser(User userInfo);

}
