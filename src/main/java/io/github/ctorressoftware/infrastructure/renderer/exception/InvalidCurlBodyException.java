package io.github.ctorressoftware.infrastructure.renderer.exception;

public class InvalidCurlBodyException extends RuntimeException {
    public InvalidCurlBodyException(String body, Throwable throwable) {
        super("Invalid request body: " + body, throwable);
    }
}
