package io.github.ctorressoftware.application.usecase.flowexecution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ctorressoftware.application.port.out.ContextManager;
import io.github.ctorressoftware.domain.model.Context;
import io.github.ctorressoftware.domain.model.ContextVariable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

// TODO: move to infrastructure if apply, as other files
public class FlowContextManager implements ContextManager {

    private final Context context;
    private final ObjectMapper objectMapper;

    public FlowContextManager(Context context, ObjectMapper objectMapper) {
        this.context = Objects.requireNonNull(context);
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Override
    public void exportVariables(String data, Map<String, String> toExport) {

        if (toExport == null || toExport.isEmpty()) {
            return;
        }

        toExport.forEach((key, value) -> context.putVariable(
                key, getValue(data, resolveValuePath(value))
        ));
    }

    @Override
    public List<ContextVariable> getVariables() {
        return context.variables();
    }

    private String resolveValuePath(String key) {
        return "/" + key.replace(".", "/");
    }

    private String getValue(String data, String valuePath) {
        try {
            JsonNode root = objectMapper.readTree(data);
            return root.at(valuePath).asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); // TODO: customize
        }
    }
}
