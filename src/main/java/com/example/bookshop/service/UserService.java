package com.example.bookshop.service;

import com.example.bookshop.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    User register(User user);

    void updateProfile(User user);

    void changePassword(User user, String newPassword);

}
