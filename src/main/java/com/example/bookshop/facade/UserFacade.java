package com.example.bookshop.facade;

import com.example.bookshop.model.User;
import com.example.bookshop.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {

    private final UserService userService;

    public UserFacade(UserService userService) {
        this.userService = userService;
    }

    public void registerNewClient(String username, String email, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(password);
        userService.register(newUser);
    }


}
