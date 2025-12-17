package dev.runnerz;

import builders.UserBuilder;
import dev.runnerz.errors.UserNotFoundException;
import dev.runnerz.repositories.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tests.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private InMemoryUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = new InMemoryUserRepository();
    }

    @Test
    public void testCreateUser() {
        User savedUser = createUser("1", "John Doe", "john@example.com");
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("John Doe", savedUser.getUsername());
        assertEquals("john@example.com", savedUser.getEmail());
    }

    @Test
    public void testFindById() {
        User savedUser = createUser("1", "Jane Doe", "jane@example.com");
        var foundUser = userRepository.findOne(savedUser.getId());
        assertEquals("Jane Doe", foundUser.getUsername());
    }

    @Test
    public void testFindById_NotFound() {
        User user = new UserBuilder()
                .id("1")
                .username("Jane Doe")
                .email("jane@example.com")
                .build();

        assertThrows(UserNotFoundException.class, () -> {
            userRepository.findOne("2");
        });
    }

    @Test
    public void updateUser() {
        User user = createUser("1", "Jane Doe", "jane@example.com");
        User updatedUser = createUser("2", "Jane Doe", "ipa@example.com");

        User updated = userRepository.updateUser(updatedUser, "1");

        assertEquals("ipa@example.com", updated.getEmail());
        assertEquals("2", updated.getId());
    }

    @Test
    public void findAllUsers() {
        User firstUser = createUser("1", "Jane Doe", "jane@example.com");
        User seconddUser = createUser("2", "Jane Doe", "ipa@example.com");

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
    }


    private User createUser(String id, String username, String email) {
        User user = new UserBuilder()
                .id(id)
                .username(username)
                .email(email)
                .build();

        return userRepository.save(user);
    }
}
