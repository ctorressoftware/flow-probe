package io.github.ctorressoftware.infrastructure.readfile.yaml;

import io.github.ctorressoftware.application.port.out.FlowFileReader;
import io.github.ctorressoftware.domain.exception.*;
import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.Flow;
import io.github.ctorressoftware.domain.model.FlowStep;
import io.github.ctorressoftware.domain.model.ServiceCall;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class YamlReader implements FlowFileReader {

    private final Yaml yaml;

    public YamlReader() {
        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(YamlFlow.class, loaderOptions);
        this.yaml = new Yaml(constructor);
    }

    @Override
    public Flow read(FilePath filePath) {

        YamlFlow yamlFlow = parseFile(filePath);

        if (yamlFlow == null) throw new EmptyFileException(filePath.value());

        String flowName = Optional
                .ofNullable(yamlFlow.getName())
                .filter(name -> !name.isBlank())
                .orElseThrow(() -> new NoFlowNameException(filePath.value())); // TODO: analyze if create generic flow exception

        List<YamlStep> steps = Optional.ofNullable(yamlFlow.getSteps())
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new NoDefinedStepsException(filePath.value())); // TODO: analyze if create generic flow exception

        List<FlowStep> formattedSteps = formatSteps(flowName, steps);

        return Flow.create(flowName, formattedSteps);
    }

    private YamlFlow parseFile(FilePath filePath) {

        try (InputStream inputStream = Files.newInputStream(Path.of(filePath.value()))) {
            return yaml.load(inputStream);
        } catch (IOException e) {
            throw new UnreadableFileException(filePath.value(), e);
        } catch (YAMLException exception) { // TODO: check if capture other SnakeYAML exceptions
            throw new InvalidYamlFileException("Could not parse YAML file: " + filePath.value(), exception);
        }
    }

    private List<FlowStep> formatSteps(String flowName, List<YamlStep> steps) {
        return steps.stream()
                .map(step -> formatStep(flowName, step))
                .toList();
    }

    private FlowStep formatStep(String flowName, YamlStep step) {

        if (step == null) {
            throw new InvalidFlowStepException("Flow step cannot be null");
        }

        if (step.getName() == null || step.getName().isBlank()) {
            throw new InvalidFlowStepException("Flow step name cannot be blank");
        }

        YamlStepRequest request = step.getRequest();

        if (request == null) {
            throw new InvalidFlowStepException("Request is required for step: " + step.getName());
        }

        if (request.getUrl() == null || request.getUrl().isBlank()) {
            throw new InvalidFlowStepException("Request url is required for step: " + step.getName());
        }

        if (request.getMethod() == null || request.getMethod().isBlank()) {
            throw new InvalidFlowStepException("Request method is required for step: " + step.getName());
        }

        return FlowStep.create(
                flowName,
                step.getName(),
                new ServiceCall(
                        step.getRequest().getUrl(),
                        step.getRequest().getMethod(),
                        step.getRequest().getHeaders(),
                        step.getRequest().getBody()
                ),
                step.getRequires(),
                step.getExports()
        );
    }
}