package io.github.ctorressoftware.application.exception;

public class JsonDeserializationException extends RuntimeException {
    public JsonDeserializationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
