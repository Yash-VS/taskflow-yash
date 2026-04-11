package com.taskflow.exception;

/** Thrown when a requested resource (user, project, task) does not exist. Maps to 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
