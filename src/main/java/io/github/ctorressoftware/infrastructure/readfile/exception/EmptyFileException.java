package io.github.ctorressoftware.infrastructure.readfile.exception;

public class EmptyFileException extends RuntimeException {
    public EmptyFileException(String filePath) {
        super("Specified YAML file is empty: " + filePath);
    }
}
