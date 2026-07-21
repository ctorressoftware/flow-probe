package io.github.ctorressoftware.infrastructure.readfile.exception;

public class NoFlowNameException extends RuntimeException {
    public NoFlowNameException(String filePath) {
        super("Could not read YAML flow name from: " + filePath);
    }
}
