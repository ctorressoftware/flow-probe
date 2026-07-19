package io.github.ctorressoftware.domain.exception;

public class UnreadableFileException extends RuntimeException {
    public UnreadableFileException(String filePath, Throwable throwable) {
        super("Could not read YAML file: " + filePath, throwable);
    }
}
