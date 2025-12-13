package dev.runnerz.repositories;

import tests.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class InMemoryUserRepository {

    private final List<User> users = new ArrayList<>();

    public User save(User user) {
        users.add(user);
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }


    public User findOne(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
