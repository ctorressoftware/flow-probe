package io.github.ctorressoftware.domain.model;

import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {

    private final Map<String, Object> variables = new HashMap<>();

    public void putVariable(String name, Object value) {
        variables.put(name, value);
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public String resolvePlaceholders(String value) {
        if (value == null) {
            return null;
        }

        String resolved = value;

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            resolved = resolved.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }

        return resolved;
    }

    public Map<String, Object> variables() {
        return Map.copyOf(variables);
    }
}