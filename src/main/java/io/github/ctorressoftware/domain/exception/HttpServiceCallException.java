package io.github.ctorressoftware.domain.exception;

public class HttpServiceCallException extends RuntimeException {
    public HttpServiceCallException(String message) {
        super(message);
    }

    public HttpServiceCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
