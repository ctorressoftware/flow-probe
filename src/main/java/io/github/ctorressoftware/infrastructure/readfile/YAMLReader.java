package io.github.ctorressoftware.infrastructure.readfile;

import io.github.ctorressoftware.application.port.out.FlowFileReader;
import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.Flow;
import io.github.ctorressoftware.domain.model.FlowStep;
import io.github.ctorressoftware.domain.model.ServiceCall;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YAMLReader implements FlowFileReader {

    @Override
    @SuppressWarnings("unchecked")
    public Flow read(FilePath filePath) {

        Yaml yaml = new Yaml();

        try (InputStream inputStream = Files.newInputStream(Path.of(filePath.value()))) {

            Map<String, Object> yamlMap = (Map<String, Object>) yaml.load(inputStream);

            String flowName = yamlMap.get("name").toString();

            List<Map<String, Object>> steps =
                    (List<Map<String, Object>>) yamlMap.get("steps");

            List<FlowStep> formattedSteps = new ArrayList<>();

            for (Map<String, Object> step : steps) {

                String stepName = step.get("name").toString();

                Map<String, Object> request = (Map<String, Object>) step.get("request");
                Map<String, Object> requires = (Map<String, Object>) step.get("requires");
                Map<String, String> export = (Map<String, String>) step.get("export");

                String url = request.get("url").toString();
                String method = request.get("method").toString();

                Map<String, String> headers =
                        (Map<String, String>) request.get("headers");

                Object body = request.get("body");

                ServiceCall serviceCall = resolveRequest(url, method, headers, body);
                FlowStep flowStep = resolveStep(flowName, stepName, serviceCall, requires, export);

                formattedSteps.add(flowStep);
            }

            return Flow.create(flowName, formattedSteps);
        } catch (IOException e) {
            throw new RuntimeException("Could not read YAML file: " + filePath.value(), e); // TODO: create custom one
        }
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