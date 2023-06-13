package com.jwt.auth.service;

import com.jwt.auth.model.User;

public interface UserService {
    public User findByUser(String userName);

    public User createUser(User userInfo);

}
