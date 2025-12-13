package dev.runnerz.errors;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseException {

    public UserAlreadyExistsException() {
        super("USER_ALREADY_EXISTS", HttpStatus.CONFLICT);
    }
}