package io.github.ctorressoftware.domain.model;

import io.github.ctorressoftware.domain.exception.DuplicateVariableException;
import io.github.ctorressoftware.domain.exception.EmptyContextException;
import io.github.ctorressoftware.domain.exception.MissingVariableException;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private final Map<String, Object> variables = new HashMap<>();

    public void putVariable(String name, Object value) {

        validateName(name);

        if (variables.containsKey(name)) {
            throw new DuplicateVariableException(name);
        }

        variables.put(name, value);
    }

    public Object getVariable(String name) {

        if (variables.isEmpty()) {
            throw new EmptyContextException();
        }

        if (!variables.containsKey(name)) {
            throw new MissingVariableException(name);
        }

        return variables.get(name);
    }

    public Map<String, Object> variables() {
        return Map.copyOf(variables);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Variable name cannot be null or blank");
        }
    }
}
