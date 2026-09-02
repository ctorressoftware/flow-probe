package io.github.ctorressoftware.application.exception;

public class JsonExtractionException extends RuntimeException {

    public JsonExtractionException(String message) {
        super(message);
    }

    public JsonExtractionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
