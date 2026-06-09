package io.github.ctorressoftware.domain.model;

import java.util.Objects;

public class FilePath {

    private final String value;

    public FilePath(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FilePath filePath = (FilePath) o;
        return Objects.equals(value, filePath.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
