package io.github.ctorressoftware.application.usecase.flowexecution;

import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.domain.model.ContextVariable;
import io.github.ctorressoftware.domain.model.ServiceCall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceholderResolver {

    private final JsonProcessor jsonProcessor;

    public PlaceholderResolver(JsonProcessor jsonProcessor) {
        this.jsonProcessor = jsonProcessor;
    }

    public ServiceCall resolve(List<ContextVariable> variables, ServiceCall serviceCall) {

        if (serviceCall == null) {
            throw new IllegalArgumentException("serviceCall cannot be null");
        }

        return new ServiceCall(
                resolveString(variables, serviceCall.url()),
                resolveString(variables, serviceCall.method()),
                resolveMap(variables, serviceCall.headers()),
                resolveBody(variables, serviceCall.body())
        );
    }

    private Object resolveBody(
            List<ContextVariable> variables,
            Object body
    ) {
        if (body == null) {
            return null;
        }

        return resolveString(variables, jsonProcessor.serialize(body));
    }

    private String resolveString(List<ContextVariable> variables, String value) {

        if (value == null) return null;

        String resolved = value;

        for (ContextVariable variable : variables) {
            resolved = resolved.replace("${" + variable.name() + "}", replacementValue(variable));
        }

        return resolved;
    }

    private Map<String, String> resolveMap(List<ContextVariable> variables, Map<String, String> stringMap) {
        if (stringMap == null) return null;

        Map<String, String> mapResolved = new HashMap<>();

        for (Map.Entry<String, String> entry : stringMap.entrySet()) {

            validateHeader(entry);
            String newKey = entry.getKey();
            String newVal = entry.getValue();

            for (ContextVariable variable : variables) {
                String placeholder = "${" + variable.name() + "}";
                String replacement = replacementValue(variable);

                newKey = newKey.replace(placeholder, replacement);
                newVal = newVal.replace(placeholder, replacement);
            }
            mapResolved.put(newKey, newVal);
        }

        return mapResolved;
    }

    private String replacementValue(ContextVariable variable) {
        return variable.value() == null
                ? ""
                : variable.value().toString();
    }

    private void validateHeader(Map.Entry<String, String> entry) {
        if (entry.getKey() == null) {
            throw new IllegalArgumentException("Header key cannot be null");
        }

        if (entry.getValue() == null) {
            throw new IllegalArgumentException(
                    "Header value cannot be null: " + entry.getKey()
            );
        }
    }
}
