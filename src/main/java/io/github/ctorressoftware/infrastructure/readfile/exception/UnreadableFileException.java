package io.github.ctorressoftware.infrastructure.readfile.exception;

public class UnreadableFileException extends RuntimeException {
    public UnreadableFileException(String filePath, Throwable throwable) {
        super("Could not read YAML file: " + filePath, throwable);
    }
}
