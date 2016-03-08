package io.ullrich.buedchen.server.exceptions;

public class ChannelAlreadyExistsException extends RuntimeException {

    public ChannelAlreadyExistsException() {
    }

    public ChannelAlreadyExistsException(String message) {
        super(message);
    }

    public ChannelAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChannelAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public ChannelAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
