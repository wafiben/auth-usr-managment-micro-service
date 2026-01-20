package dev.runnerz;

import DTO.UserEventRequest;
import builders.UserBuilder;
import dev.runnerz.eventservice.repositories.InMemoryEventServiceClient;
import dev.runnerz.repositories.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tests.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthTest {

    private InMemoryUserRepository userRepository;
    private InMemoryEventServiceClient eventClient;

    @BeforeEach
    public void setUp() {
        eventClient = new InMemoryEventServiceClient();
        userRepository = new InMemoryUserRepository(eventClient);
    }

    @Test
    public void testRegister_Success() {

        User user = new UserBuilder()
                .id("1")
                .email("example@example.com")
                .username("wafi")
                .password("securePassword")
                .build();

        var savedUser = userRepository.save(user);

        UserEventRequest firstEvent = eventClient.getEvents().get(0);

        assertEquals("1", firstEvent.getUserId());
        assertEquals("example@example.com", firstEvent.getEmail());
        assertEquals("wafi", firstEvent.getUsername());

        var foundUser = userRepository.findOne("1");
        assertEquals("example@example.com", foundUser.getEmail());
    }
}
