package io.github.ctorressoftware.infrastructure.readfile;

import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.Flow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YAMLReaderTest {
    private static final String BASE_PATH = "src/test/resources/yaml-cases/";
    private final YAMLReader reader = new YAMLReader();

    @Test
    void parsesFullyValidFlow() {
        Flow flow = reader.read(new FilePath(BASE_PATH + "fully-valid-flow.yaml"));
        assertEquals("pokeapi-success-flow", flow.getName());
        assertEquals(3, flow.getSteps().size());
    }

    @Test
    void parsesPartiallyValidFlow() {
        Flow flow = reader.read(new FilePath(BASE_PATH + "partially-valid-flow.yaml"));
        assertEquals("pokeapi-partially-success-flow", flow.getName());
        assertEquals(2, flow.getSteps().size());
    }

    @Test
    void parsesFailureFlow() {
        Flow flow = reader.read(new FilePath(BASE_PATH + "failure-flow.yaml"));
        assertEquals("pokeapi-failure-flow", flow.getName());
        assertEquals(1, flow.getSteps().size());
    }
}
