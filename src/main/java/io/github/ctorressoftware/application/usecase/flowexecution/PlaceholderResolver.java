package io.github.ctorressoftware.application.usecase.flowexecution;

import io.github.ctorressoftware.domain.exception.MissingVariableException;
import io.github.ctorressoftware.domain.model.ContextVariable;
import io.github.ctorressoftware.domain.model.ServiceCall;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlaceholderResolver {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    public ServiceCall resolve(List<ContextVariable> variables, ServiceCall serviceCall) {

        if (serviceCall == null) {
            throw new IllegalArgumentException("serviceCall cannot be null");
        }

        return new ServiceCall(
                resolveText(variables, serviceCall.url()),
                resolveText(variables, serviceCall.method()),
                resolveHeaders(variables, serviceCall.headers()),
                resolveBodyValue(variables, serviceCall.body())
        );
    }

    private Object resolveBodyValue(List<ContextVariable> variables, Object value) {
        return switch (value) {
            case null -> null;
            case String stringValue -> resolveBodyString(variables, stringValue);
            case Map<?, ?> map -> resolveBodyMap(variables, map);
            case List<?> list -> list.stream()
                    .map(item -> resolveBodyValue(variables, item))
                    .toList();
            default -> value;
        };
    }

    private Object resolveBodyString(List<ContextVariable> variables, String value) {

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);

        if (matcher.matches()) {
            ContextVariable variable = findVariable(variables, matcher.group(1));
            return variable.value();
        }

        return resolveText(variables, value);
    }

    private Map<String, Object> resolveBodyMap(List<ContextVariable> variables, Map<?, ?> body) {
        Map<String, Object> resolved = new LinkedHashMap<>();

        for (Map.Entry<?, ?> entry : body.entrySet()) {

            if (!(entry.getKey() instanceof String key)) {
                throw new IllegalArgumentException("Request body object keys must be strings");
            }

            resolved.put(
                    resolveText(variables, key),
                    resolveBodyValue(
                            variables,
                            entry.getValue()
                    )
            );
        }

        return resolved;
    }

    private Map<String, String> resolveHeaders(List<ContextVariable> variables, Map<String, String> headers) {

        if (headers == null) {
            return Map.of();
        }

        Map<String, String> resolved = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            validateHeader(entry);

            resolved.put(
                    resolveText(variables, entry.getKey()),
                    resolveText(variables, entry.getValue())
            );
        }

        return resolved;
    }

    private String resolveText(List<ContextVariable> variables, String value) {

        if (value == null) {
            return null;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        StringBuilder resolved = new StringBuilder();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            ContextVariable variable = findVariable(variables, variableName);
            String replacement = String.valueOf(variable.value());

            matcher.appendReplacement(resolved, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(resolved);

        return resolved.toString();
    }

    private ContextVariable findVariable(List<ContextVariable> variables, String variableName) {

        if (variables == null) {
            throw new IllegalArgumentException("variables cannot be null");
        }

        return variables.stream()
                .filter(variable ->
                        variable.name().equals(variableName))
                .findFirst()
                .orElseThrow(() ->
                        new MissingVariableException(variableName));
    }

    private void validateHeader(Map.Entry<String, String> entry) {
        if (entry.getKey() == null) {
            throw new IllegalArgumentException("Header key cannot be null");
        }

        if (entry.getValue() == null) {
            throw new IllegalArgumentException("Header value cannot be null: " + entry.getKey());
        }
    }
}
