package io.github.ctorressoftware.application.usecase.flowexecution;

import io.github.ctorressoftware.application.port.out.JsonProcessor;
import io.github.ctorressoftware.domain.model.Context;
import io.github.ctorressoftware.domain.model.ContextVariable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ContextManager {

    private final Context context;
    private final JsonProcessor jsonProcessor;

    public ContextManager(Context context, JsonProcessor jsonProcessor) {
        this.context = Objects.requireNonNull(context);
        this.jsonProcessor = Objects.requireNonNull(jsonProcessor);
    }

    public void exportVariables(String data, Map<String, String> toExport) {

        if (toExport == null || toExport.isEmpty()) {
            return;
        }

        toExport.forEach((key, value) ->
                context.putVariable(key, jsonProcessor.extractValue(data, resolveValuePath(value)))
        );
    }

    public List<ContextVariable> getVariables() {
        return context.variables();
    }

    private String resolveValuePath(String key) {
        return "/" + key.replace(".", "/");
    }
}
