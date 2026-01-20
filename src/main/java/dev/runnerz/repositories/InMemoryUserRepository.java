package dev.runnerz.repositories;

import dev.runnerz.eventservice.repositories.InMemoryEventServiceClient;
import tests.User;
import dev.runnerz.errors.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;


public class InMemoryUserRepository {
    private InMemoryEventServiceClient eventClient;

    private final List<User> users = new ArrayList<>();

    public InMemoryUserRepository(InMemoryEventServiceClient eventClient) {
        this.eventClient = eventClient;
    }

    public User save(User user) {
        users.add(user);

        eventClient.publishUserRegistered(
                user.getId(),
                user.getEmail(),
                user.getUsername()
        );
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public User findOne(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
    }

    public User updateUser(User user, String id) {
        var existingUser = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);

        users.remove(existingUser);
        users.add(user);
        return user;

    }
}
