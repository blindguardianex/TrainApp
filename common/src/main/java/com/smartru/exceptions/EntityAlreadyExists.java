package com.smartru.exceptions;

public class EntityAlreadyExists extends Exception {

    public EntityAlreadyExists() {
        super();
    }

    public EntityAlreadyExists(String message) {
        super(message);
    }

    public EntityAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }
}
