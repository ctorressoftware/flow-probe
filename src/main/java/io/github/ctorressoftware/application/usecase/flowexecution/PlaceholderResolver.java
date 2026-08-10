package io.github.ctorressoftware.application.usecase.flowexecution;

import io.github.ctorressoftware.domain.model.ContextVariable;
import io.github.ctorressoftware.domain.model.ServiceCall;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceholderResolver {

    private final JsonUtils jsonUtils;

    public PlaceholderResolver(JsonUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
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

        return resolveString(variables, jsonUtils.serialize(body));
    }

    public String resolveString(List<ContextVariable> variables, String value) {

        if (value == null) return null;

        String resolved = value;

        for (ContextVariable variable : variables) {
            resolved = resolved.replace("${" + variable.name() + "}", String.valueOf(variable.value()));
        }

        return resolved;
    }

    public Map<String, String> resolveMap(List<ContextVariable> variables, Map<String, String> stringMap) {
        if (stringMap == null) return null;

        Map<String, String> mapResolved = new HashMap<>();

        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            String newKey = entry.getKey();
            String newVal = entry.getValue();

            for (ContextVariable variable : variables) {
                String placeholder = "${" + variable.name() + "}";
                String replacement = variable.value() != null ? variable.value().toString() : "";

                newKey = newKey.replace(placeholder, replacement);
                if (newVal != null) {
                    newVal = newVal.replace(placeholder, replacement);
                }
            }
            mapResolved.put(newKey, newVal);
        }

        return mapResolved;
    }
}
