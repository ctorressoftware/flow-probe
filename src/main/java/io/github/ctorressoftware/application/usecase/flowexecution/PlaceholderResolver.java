package io.github.ctorressoftware.application.usecase.flowexecution;

import io.github.ctorressoftware.domain.model.ContextVariable;

import java.util.List;

public class PlaceholderResolver {

    public static String resolve(List<ContextVariable> variables, String value) {
        if (value == null) return null;

        String resolved = value;

        for (ContextVariable variable : variables) {
            resolved = resolved.replace("${" + variable.name() + "}", String.valueOf(variable.value()));
        }

        return resolved;
    }
}
