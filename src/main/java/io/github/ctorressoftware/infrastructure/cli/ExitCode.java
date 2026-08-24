package io.github.ctorressoftware.infrastructure.cli;

public enum ExitCode {
    SUCCESS(0),
    EXECUTION_ERROR(1),
    INVALID_ARGUMENTS(2);

    private final int code;

    ExitCode(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
