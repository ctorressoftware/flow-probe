package io.github.ctorressoftware.infrastructure.readfile;

import io.github.ctorressoftware.application.port.out.FlowFileReader;
import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.Flow;
import io.github.ctorressoftware.domain.model.ServiceCall;
import io.github.ctorressoftware.domain.model.FlowStep;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YAMLReader implements FlowFileReader {

    @SuppressWarnings("unchecked")
    public Flow read(FilePath filePath) {

        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(filePath.value());

        Map<String, Object> yamlMap = (Map<String, Object>) yaml.load(inputStream);

        String flowName = yamlMap.get("name").toString();

        List<Map<String, Object>> steps = (List<Map<String, Object>>) yamlMap.get("steps");

        List<FlowStep> formattedSteps = new ArrayList<>();

        for (Map<String, Object> step : steps) {
            String stepName = step.get("name").toString();

            Map<String, Object> request = (Map<String, Object>) step.get("request");
            Map<String, Object> expect = (Map<String, Object>) step.get("expect");
            Map<String, String> export = (Map<String, String>) step.get("export");

            String url = request.get("url").toString();
            String method = request.get("method").toString();
            Map<String, String> headers = (Map<String, String>) request.get("headers");
            Object body = request.get("body");

            ServiceCall serviceCall = resolveRequest(url, method, headers, body);
            FlowStep flowStep = resolveStep(flowName, stepName, serviceCall, expect, export);
            formattedSteps.add(flowStep);
        }

        return Flow.create(flowName, formattedSteps);
    }

    private ServiceCall resolveRequest(
            String url,
            String method,
            Map<String, String> headers,
            Object body
    ) {
        return new ServiceCall(
                url,
                method,
                headers,
                body
        );
    }

    private FlowStep resolveStep(
            String flowName,
            String stepName,
            ServiceCall serviceCall,
            Map<String, Object> expect,
            Map<String, String> export
    ) {
        return FlowStep.create(
                flowName,
                stepName,
                serviceCall,
                expect,
                export
        );
    }
}
