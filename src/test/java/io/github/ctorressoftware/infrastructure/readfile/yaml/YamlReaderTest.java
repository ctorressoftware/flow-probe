package io.github.ctorressoftware.infrastructure.readfile.yaml;

import io.github.ctorressoftware.domain.model.*;
import io.github.ctorressoftware.infrastructure.readfile.exception.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class YamlReaderTest {
    private static final String BASE_PATH = "src/test/resources/yaml-cases/";
    private final YamlReader reader = new YamlReader();

    @Test
    void parsesFullyValidFlow() {

        Flow expected = Flow.create(
                "pokeapi-success-flow",
                List.of(
                        FlowStep.create(
                                "pokeapi-success-flow",
                                "get-all-pokemon",
                                new ServiceCall(
                                        "https://pokeapi.co/api/v2/pokemon?offset=0&limit=1350",
                                        "GET",
                                        Map.of("accept", "application/json"),
                                        null
                                ),
                                new ExpectedResponse(
                                        200,
                                        List.of(
                                                new BodyExpectation(
                                                        "/results/0/name",
                                                        ExpectationOperator.EQUALS,
                                                        "bulbasaur"
                                                ),
                                                new BodyExpectation(
                                                        "/count",
                                                        ExpectationOperator.NOT_EQUALS,
                                                        "0"
                                                )
                                        )
                                ),
                                Map.of("pokemonName", "/results/0/name")
                        ),
                        FlowStep.create(
                                "pokeapi-success-flow",
                                "get-pokemon",
                                new ServiceCall(
                                        "https://pokeapi.co/api/v2/pokemon/${pokemonName}",
                                        "GET",
                                        Map.of("accept", "application/json"),
                                        null
                                ),
                                new ExpectedResponse(
                                        200,
                                        List.of(
                                                new BodyExpectation(
                                                        "/name",
                                                        ExpectationOperator.EQUALS,
                                                        "bulbasaur"
                                                )
                                        )
                                ),
                                null
                        ),
                        FlowStep.create(
                                "pokeapi-success-flow",
                                "get-pikachu",
                                new ServiceCall(
                                        "https://pokeapi.co/api/v2/pokemon/pikachu",
                                        "GET",
                                        Map.of("accept", "application/json"),
                                        null
                                ),
                                new ExpectedResponse(200, List.of()),
                                null
                        )
                )
        );

        Assertions.assertEquals(
                expected,
                reader.read(new FilePath(BASE_PATH + "fully-valid-flow.yaml"))
        );
    }

    @Test
    void parsesResponseExpectations() {

        Flow flow = reader.read(
                new FilePath(BASE_PATH + "fully-valid-flow.yaml")
        );

        ExpectedResponse expectedResponse = flow.steps().getFirst().expectedResponse();

        Assertions.assertEquals(200, expectedResponse.status()
        );

        Assertions.assertEquals(
                List.of(
                        new BodyExpectation(
                                "/results/0/name",
                                ExpectationOperator.EQUALS,
                                "bulbasaur"
                        ),
                        new BodyExpectation(
                                "/count",
                                ExpectationOperator.NOT_EQUALS,
                                "0"
                        )
                ),
                expectedResponse.bodyExpectations()
        );
    }

    @Test
    void rejectsEmptyFlowFile() {
        FilePath filePath = new FilePath(BASE_PATH + "empty-flow.yaml");

        EmptyFileException exception = Assertions.assertThrows(
                EmptyFileException.class,
                () -> reader.read(filePath)
        );

        Assertions.assertEquals(
                "Specified YAML file is empty: " + filePath.value(),
                exception.getMessage()
        );
    }

    @Test
    void rejectsCorruptFlowFile() {
        FilePath filePath = new FilePath(BASE_PATH + "corrupted-flow.yaml");

        InvalidYamlFileException exception = Assertions.assertThrows(
                InvalidYamlFileException.class,
                () -> reader.read(filePath)
        );

        Assertions.assertEquals(
                "Could not parse YAML file: " + filePath.value(),
                exception.getMessage()
        );
    }

    @Test
    void rejectsFlowWithoutName() {
        FilePath filePath = new FilePath(BASE_PATH + "no-name-flow.yaml");

        NoFlowNameException exception = Assertions.assertThrows(
                NoFlowNameException.class,
                () -> reader.read(filePath)
        );

        Assertions.assertEquals(
                "Could not read YAML flow name from: " + filePath.value(),
                exception.getMessage()
        );
    }

    @Test
    void rejectsFlowWithoutSteps() {
        FilePath filePath = new FilePath(BASE_PATH + "flow-without-steps.yaml");

        NoDefinedStepsException exception = Assertions.assertThrows(
                NoDefinedStepsException.class,
                () -> reader.read(filePath)
        );

        Assertions.assertEquals(
                "No defined steps in the specified file: " + filePath.value(),
                exception.getMessage()
        );
    }

    @Test
    void rejectsFlowWithoutStepName() {
        FilePath filePath = new FilePath(BASE_PATH + "flow-without-step-name.yaml");

        InvalidFlowStepException exception = Assertions.assertThrows(
                InvalidFlowStepException.class,
                () -> reader.read(filePath)
        );

        Assertions.assertEquals(
                "Flow step name cannot be blank",
                exception.getMessage()
        );
    }

    @Test
    void rejectsFlowWithoutStepRequest() {
        FilePath filePath = new FilePath(BASE_PATH + "flow-without-step-request.yaml");

        InvalidFlowStepException exception = Assertions.assertThrows(
                InvalidFlowStepException.class,
                () -> reader.read(filePath)
        );

        Assertions.assertEquals(
                "Request is required for step: invalid-step",
                exception.getMessage()
        );
    }

    @Test
    void rejectsFlowWithoutStepRequestUrl() {
        FilePath filePath = new FilePath(BASE_PATH + "flow-without-step-request-url.yaml");

        InvalidFlowStepException exception = Assertions.assertThrows(
                InvalidFlowStepException.class,
                () -> reader.read(filePath)
        );

        Assertions.assertEquals(
                "Request url is required for step: invalid-request-url",
                exception.getMessage()
        );
    }

    @Test
    void rejectsFlowWithoutStepRequestMethod() {
        FilePath filePath = new FilePath(BASE_PATH + "flow-without-step-request-method.yaml");

        InvalidFlowStepException exception = Assertions.assertThrows(
                InvalidFlowStepException.class,
                () -> reader.read(filePath)
        );

        Assertions.assertEquals(
                "Request method is required for step: invalid-request-method",
                exception.getMessage()
        );
    }

    @Test
    void rejectsNonExistingFile() {
        FilePath filePath = new FilePath(BASE_PATH + "404.yaml");

        UnreadableFileException exception = Assertions.assertThrows(
                UnreadableFileException.class,
                () -> reader.read(filePath)
        );

        Assertions.assertEquals(
                "Could not read YAML file: " + filePath.value(),
                exception.getMessage()
        );
    }

    @Test
    void rejectsWrongYamlFlowStructure() {
        FilePath filePath = new FilePath(BASE_PATH + "wrong-yaml-flow-structure.yaml");

        InvalidYamlFileException exception = Assertions.assertThrows(
                InvalidYamlFileException.class,
                () -> reader.read(filePath)
        );

        Assertions.assertEquals(
                "Could not parse YAML file: " + filePath.value(),
                exception.getMessage()
        );
    }
}
