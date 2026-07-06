package io.github.ctorressoftware.application.usecase.flowexecution;

import java.util.Map;

public class PlaceholderResolver {

    public static String resolve(Map<String, Object> variables, String value) {
        if (value == null) return null;

        String resolved = value;

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            resolved = resolved.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }

        return resolved;
    }

}
