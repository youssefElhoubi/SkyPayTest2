package com.service;

import com.entities.User;
import com.exceptions.InvalidInputException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {


    private Optional<User> findUserById(List<User> users, String userId) {
        if (users == null || userId == null) return Optional.empty();
        return users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst();
    }

    public void createUser(List<User> users, int balance) {
        if (balance < 0) {
            throw new InvalidInputException("Balance cannot be negative.");
        }
        String newId = UUID.randomUUID().toString();
        User newUser = new User(newId, balance);

        users.add(newUser);
        System.out.println("✅ User created successfully with ID: " + newUser.getId());
    }
    public void updateUser(List<User> users, int balance, String id) {
        if (balance < 0) {
            throw new InvalidInputException("Balance cannot be negative.");
        }
        User user = findUserById(users, id)
                .orElseThrow(() -> new InvalidInputException("User with ID " + id + " not found."));

        user.setBalance(balance);
        System.out.println("✅ User " + id + " updated. New Balance: " + balance);
    }

    public void listAllUsers(List<User> users) {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║                    USER DIRECTORY                    ║");
        System.out.println("╠══════════════════════════════════════╤═══════════════╣");
        System.out.println("║               USER ID                │    BALANCE    ║");
        System.out.println("╠══════════════════════════════════════╪═══════════════╣");

        if (users == null || users.isEmpty()) {
            System.out.println("║             No users registered yet.                 ║");
        } else {

            for (int i = users.size() - 1; i >= 0; i--) {
                User u = users.get(i);
                System.out.printf("║ %-36s │ %13d ║%n",
                        u.getId(),     // Prints the UUID string
                        u.getBalance() // Prints the integer balance
                );
            }
        }
        System.out.println("╚══════════════════════════════════════╧═══════════════╝");
    }
}