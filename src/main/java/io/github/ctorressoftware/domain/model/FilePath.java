package io.github.ctorressoftware.domain.model;

import java.util.Objects;

public record FilePath(String value) {

    public FilePath(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FilePath filePath = (FilePath) o;
        return Objects.equals(value, filePath.value);
    }
}
