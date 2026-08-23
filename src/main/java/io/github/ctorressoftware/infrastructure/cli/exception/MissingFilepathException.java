package io.github.ctorressoftware.infrastructure.cli.exception;

public class MissingFilepathException extends RuntimeException {
    public MissingFilepathException() {
        super("A file path must be provided");
    }
}
