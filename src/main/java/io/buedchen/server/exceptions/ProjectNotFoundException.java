package io.buedchen.server.exceptions;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException() {
    }

    public ProjectNotFoundException(String message) {
        super(message);
    }
}
