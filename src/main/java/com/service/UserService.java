package com.service;

import com.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private Optional<User> findUserById(List<User> users, String userId) {
        return users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst();
    }

    public void setUser(List<User> users, int balance) { //

        User newUser = new User(UUID.randomUUID().toString(), balance);
        users.add(newUser);
        System.out.println("✅ User " + newUser.getId() + " created successfully: " + newUser);
    }
}

