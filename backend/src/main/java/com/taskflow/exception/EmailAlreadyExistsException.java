package com.taskflow.exception;

/** Thrown on registration when the submitted email is already registered. Maps to 409 Conflict. */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
