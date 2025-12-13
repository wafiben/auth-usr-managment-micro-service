package dev.runnerz.repositories;

import dev.runnerz.models.User;

import java.util.ArrayList;
import java.util.List;

public class InMemoryUserRepository {

    private final List<User> users = new ArrayList<>();

    public User save(User user) {
        users.add(user);
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }
}
