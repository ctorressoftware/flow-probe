package io.github.ctorressoftware.infrastructure.readfile;

import io.github.ctorressoftware.domain.exception.InvalidFlowStepException;
import io.github.ctorressoftware.domain.exception.NoDefinedStepsException;
import io.github.ctorressoftware.domain.exception.NoFlowNameException;
import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.Flow;
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
}
