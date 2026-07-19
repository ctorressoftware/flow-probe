package io.github.ctorressoftware.infrastructure.readfile;

import io.github.ctorressoftware.application.port.out.FlowFileReader;
import io.github.ctorressoftware.domain.exception.EmptyFileException;
import io.github.ctorressoftware.domain.exception.NoDefinedStepsException;
import io.github.ctorressoftware.domain.exception.NoFlowNameException;
import io.github.ctorressoftware.domain.exception.UnreadableFileException;
import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.Flow;
import io.github.ctorressoftware.domain.model.FlowStep;
import io.github.ctorressoftware.domain.model.ServiceCall;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YAMLReader implements FlowFileReader {

    private final Yaml yaml;

    public YAMLReader() {
        LoaderOptions loaderOptions = new LoaderOptions();

        Constructor constructor = new Constructor(
                FlowYaml.class,
                loaderOptions
        );

        this.yaml = new Yaml(constructor);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Flow read(FilePath filePath) {

        Map<String, Object> yamlMap = parseFile(filePath);

        if (yamlMap.isEmpty()) throw new EmptyFileException(filePath.value());

        String flowName = yamlMap.getOrDefault("name", null).toString();

        if (flowName.isBlank()) throw new NoFlowNameException(filePath.value());

        Object rawSteps = yamlMap.getOrDefault("steps", null);

        if (rawSteps == null || rawSteps.equals("")) throw new NoDefinedStepsException(filePath.value());

        List<Map<String, Object>> steps = (List<Map<String, Object>>) rawSteps; // TODO: improve with SnakeYAML and DTOs

        List<FlowStep> formattedSteps = formatSteps(flowName, steps);

        return Flow.create(flowName, formattedSteps);
    }

    private Map<String, Object> parseFile(FilePath filePath) {
        
        try (InputStream inputStream = Files.newInputStream(Path.of(filePath.value()))) {
            return yaml.load(inputStream);
        } catch (IOException e) {
            throw new UnreadableFileException("Could not read YAML file: " + filePath.value(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<FlowStep> formatSteps(String flowName, List<Map<String, Object>> steps) {
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
        return formattedSteps;
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