package io.github.ctorressoftware.domain.model;

import io.github.ctorressoftware.domain.exception.DuplicateVariableException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Context {
    private final List<ContextVariable> variables = new ArrayList<>();

    public void putVariable(String name, Object value) {
        validateName(name);
        ContextVariable variable = new ContextVariable(name, value);
        variables.add(variable);
    }

    public List<ContextVariable> variables() {
        return Collections.unmodifiableList(variables);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Variable name cannot be null or blank");
        }

        if (variables.stream().anyMatch(v -> v.name().equals(name))) {
            throw new DuplicateVariableException(name);
        }
    }
}
