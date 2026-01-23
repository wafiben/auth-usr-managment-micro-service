package endtoendtest;

import dev.runnerz.RunnerzApplication;
import dev.runnerz.models.User;
import dev.runnerz.repositories.UserRepository;
import DTO.RegisterRequest;
import DTO.AuthResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = RunnerzApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class UserE2ETest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @AfterEach
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    public void testCreateUser_WithAuthentication() {
        // Step 1: Register and get JWT token
        String token = registerAndGetToken("Wafi", "wafi@example.com", "wafi", "securePassword");

        // Step 2: Create a new user with authentication
        User newUser = new User("John Doe", "john@example.com", "johndoe", "password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<User> request = new HttpEntity<>(newUser, headers);

        ResponseEntity<User> response = restTemplate.exchange(
                "/users",
                HttpMethod.POST,
                request,
                User.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("john@example.com", response.getBody().getEmail());
        assertEquals("John Doe", response.getBody().getName());
    }


    @Test
    public void testGetOneUserFailsWithAuthentication() {
        // Step 1: Register and get JWT token
        String token = registerAndGetToken("Wafi", "wafi@example.com", "wafi", "securePassword");
        // Step 2: Create a new user with authentication
        User newUser = new User("John Doe", "john@example.com", "johndoe", "password123");

        User secondUser = new User("opa", "opa@example.com", "johndoe", "password123");


        var user = userRepository.save(newUser);
        var userId = user.getId();
        userRepository.save(secondUser);


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);


        ResponseEntity<User> response = restTemplate.exchange(
                "/users/" + "12",
                HttpMethod.GET,
                request,
                User.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAllUsersWithAuthentication() {
        // Step 1: Register and get JWT token
        String token = registerAndGetToken("Wafi", "wafi@example.com", "wafi", "securePassword");
        // Step 2: Create a new user with authentication
        User newUser = new User("John Doe", "john@example.com", "johndoe", "password123");
        User secondUser = new User("opa", "opa@example.com", "johndoe", "password123");

        userRepository.save(newUser);
        userRepository.save(secondUser);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);


        ResponseEntity<List<User>> response = restTemplate.exchange(
                "/users",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<User>>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetOneUserWithAuthentication() {
        // Step 1: Register and get JWT token
        String token = registerAndGetToken("Wafi", "wafi@example.com", "wafi", "securePassword");

        // Step 2: Create a new user with authentication
        User newUser = new User("John Doe", "john@example.com", "johndoe", "password123");

        User secondUser = new User("opa", "opa@example.com", "johndoe", "password123");

        var user = userRepository.save(newUser);
        var userId = user.getId();
        userRepository.save(secondUser);


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);


        ResponseEntity<User> response = restTemplate.exchange(
                "/users/" + userId,
                HttpMethod.GET,
                request,
                User.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertEquals("john@example.com", response.getBody().getEmail());
    }


    private String registerAndGetToken(String name, String email, String username, String password) {
        RegisterRequest request = new RegisterRequest();
        request.setName(name);
        request.setEmail(email);
        request.setUsername(username);
        request.setPassword(password);

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/auth/register",
                request,
                AuthResponse.class
        );

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
        return response.getBody().getToken();
    }
}