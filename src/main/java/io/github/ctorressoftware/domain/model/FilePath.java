package io.github.ctorressoftware.domain.model;

import java.util.Objects;

public record FilePath(String value) {

    public FilePath(String value) {
        this.value = Objects.requireNonNull(value);
    }
}
