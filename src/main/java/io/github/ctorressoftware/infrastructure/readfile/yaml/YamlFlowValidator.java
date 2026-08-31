package io.github.ctorressoftware.infrastructure.readfile.yaml;

import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.infrastructure.readfile.exception.EmptyFileException;
import io.github.ctorressoftware.infrastructure.readfile.exception.InvalidFlowStepException;
import io.github.ctorressoftware.infrastructure.readfile.exception.NoDefinedStepsException;
import io.github.ctorressoftware.infrastructure.readfile.exception.NoFlowNameException;

import java.util.List;

public final class YamlFlowValidator {

    public void validate(FilePath filePath, YamlFlow yamlFlow) {
        if (yamlFlow == null) {
            throw new EmptyFileException(filePath.value());
        }
        validateFlowName(filePath, yamlFlow);
        validateSteps(filePath, yamlFlow.getSteps());
    }

    private void validateFlowName(FilePath filePath, YamlFlow yamlFlow) {
        if (yamlFlow.getName() == null || yamlFlow.getName().isBlank()) {
            throw new NoFlowNameException(filePath.value());
        }
    }

    private void validateSteps(FilePath filePath, List<YamlStep> steps) {
        if (steps == null || steps.isEmpty()) {
            throw new NoDefinedStepsException(filePath.value());
        }

        steps.forEach(this::validateStep);
    }

    private void validateStep(YamlStep yamlStep) {

        if (yamlStep == null) {
            throw new InvalidFlowStepException("Flow step cannot be null");
        }

        if (yamlStep.getName() == null || yamlStep.getName().isBlank()) {
            throw new InvalidFlowStepException("Flow step name cannot be blank");
        }

        validateRequest(yamlStep);
    }

    private void validateRequest(YamlStep yamlStep) {

        YamlStepRequest request = yamlStep.getRequest();

        if (request == null) {
            throw new InvalidFlowStepException("Request is required for step: " + yamlStep.getName());
        }

        if (request.getUrl() == null || request.getUrl().isBlank()) {
            throw new InvalidFlowStepException("Request url is required for step: " + yamlStep.getName());
        }

        if (request.getMethod() == null || request.getMethod().isBlank()) {
            throw new InvalidFlowStepException("Request method is required for step: " + yamlStep.getName());
        }
    }
}