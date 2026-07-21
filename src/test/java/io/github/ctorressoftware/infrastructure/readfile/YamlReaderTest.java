package io.github.ctorressoftware.infrastructure.readfile;

import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.Flow;
import io.github.ctorressoftware.infrastructure.readfile.exception.*;
import io.github.ctorressoftware.infrastructure.readfile.yaml.YamlReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class YamlReaderTest {
    private static final String BASE_PATH = "src/test/resources/yaml-cases/";
    private final YamlReader reader = new YamlReader();

    @Test
    void parsesFullyValidFlow() {
        Flow flow = reader.read(new FilePath(BASE_PATH + "fully-valid-flow.yaml"));
        assertEquals("pokeapi-success-flow", flow.getName());
        assertEquals(3, flow.getSteps().size());
    }

    @Test
    void rejectsEmptyFlowFile() {
        FilePath filePath = new FilePath(BASE_PATH + "empty-flow.yaml");

        EmptyFileException exception = assertThrows(
                EmptyFileException.class,
                () -> reader.read(filePath)
        );

        assertEquals(
                "Specified YAML file is empty: " + filePath.value(),
                exception.getMessage()
        );
    }

    @Test
    void rejectsCorruptFlowFile() {
        FilePath filePath = new FilePath(BASE_PATH + "corrupted-flow.yaml");

        InvalidYamlFileException exception = assertThrows(
                InvalidYamlFileException.class,
                () -> reader.read(filePath)
        );

        assertEquals(
                "Could not parse YAML file: " + filePath.value(),
                exception.getMessage()
        );
    }

    @Test
    void rejectsFlowWithoutName() {
        FilePath filePath = new FilePath(BASE_PATH + "no-name-flow.yaml");

        NoFlowNameException exception = assertThrows(
                NoFlowNameException.class,
                () -> reader.read(filePath)
        );

        assertEquals(
                "Could not read YAML flow name from: " + filePath.value(),
                exception.getMessage()
        );
    }

    @Test
    void rejectsFlowWithoutSteps() {
        FilePath filePath = new FilePath(BASE_PATH + "flow-without-steps.yaml");

        NoDefinedStepsException exception = assertThrows(
                NoDefinedStepsException.class,
                () -> reader.read(filePath)
        );

        assertEquals(
                "No defined steps in the specified file: " + filePath.value(),
                exception.getMessage()
        );
    }

    @Test
    void rejectsFlowWithoutStepName() {
        FilePath filePath = new FilePath(BASE_PATH + "flow-without-step-name.yaml");

        InvalidFlowStepException exception = assertThrows(
                InvalidFlowStepException.class,
                () -> reader.read(filePath)
        );

        assertEquals(
                "Flow step name cannot be blank",
                exception.getMessage()
        );
    }

    @Test
    void rejectsFlowWithoutStepRequest() {
        FilePath filePath = new FilePath(BASE_PATH + "flow-without-step-request.yaml");

        InvalidFlowStepException exception = assertThrows(
                InvalidFlowStepException.class,
                () -> reader.read(filePath)
        );

        assertEquals(
                "Request is required for step: invalid-step",
                exception.getMessage()
        );
    }

    @Test
    void rejectsFlowWithoutStepRequestUrl() {
        FilePath filePath = new FilePath(BASE_PATH + "flow-without-step-request-url.yaml");

        InvalidFlowStepException exception = assertThrows(
                InvalidFlowStepException.class,
                () -> reader.read(filePath)
        );

        assertEquals(
                "Request url is required for step: invalid-request-url",
                exception.getMessage()
        );
    }

    @Test
    void rejectsFlowWithoutStepRequestMethod() {
        FilePath filePath = new FilePath(BASE_PATH + "flow-without-step-request-method.yaml");

        InvalidFlowStepException exception = assertThrows(
                InvalidFlowStepException.class,
                () -> reader.read(filePath)
        );

        assertEquals(
                "Request method is required for step: invalid-request-method",
                exception.getMessage()
        );
    }

    @Test
    void rejectsWrongYamlFlowStructure() {
        FilePath filePath = new FilePath(BASE_PATH + "wrong-yaml-flow-structure");

        UnreadableFileException exception = assertThrows(
                UnreadableFileException.class,
                () -> reader.read(filePath)
        );

        assertEquals(
                "Could not read YAML file: " + filePath.value(),
                exception.getMessage()
        );
    }
}
