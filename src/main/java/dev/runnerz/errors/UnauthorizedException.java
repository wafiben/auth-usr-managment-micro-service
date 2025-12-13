package dev.runnerz.errors;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException() {
        super("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
}