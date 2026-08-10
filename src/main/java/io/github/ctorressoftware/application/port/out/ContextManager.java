package io.github.ctorressoftware.application.port.out;

import io.github.ctorressoftware.domain.model.ContextVariable;

import java.util.List;
import java.util.Map;

public interface ContextManager {
    void exportVariables(String data, Map<String, String> toExport);
    List<ContextVariable> getVariables();
}
