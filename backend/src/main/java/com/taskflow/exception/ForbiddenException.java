package com.taskflow.exception;

/** Thrown when the authenticated user attempts an action they are not permitted to perform. Maps to 403. */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
