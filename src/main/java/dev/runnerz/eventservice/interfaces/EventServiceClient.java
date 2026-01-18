package dev.runnerz.eventservice.interfaces;

public interface EventServiceClient {
    void publishUserRegistered(String userId, String email, String username);
}
