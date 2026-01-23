package dev.runnerz.eventservice.repositories;

import DTO.UserEventRequest;
import dev.runnerz.eventservice.interfaces.EventServiceClient;

import java.util.ArrayList;
import java.util.List;

public class InMemoryEventServiceClient implements EventServiceClient {

    private final List<UserEventRequest> events = new ArrayList<>();

    @Override
    public void publishUserRegistered(String userId, String email, String username) {
        events.add(new UserEventRequest(userId, email, username));
    }

    public List<UserEventRequest> getEvents() {
        return new ArrayList<>(events);
    }
}