package io.github.ctorressoftware.infrastructure.readfile;

import io.github.ctorressoftware.domain.model.FilePath;
import io.github.ctorressoftware.domain.model.Flow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class YAMLReaderTest {
    private static final String BASE_PATH = "src/test/resources/yaml-cases/";

    @Test
    void parsesValidFullFlow() {
        YAMLReader reader = new YAMLReader();

        Flow flow = reader.read(new FilePath(BASE_PATH + "valid-full.yaml"));

        assertEquals("pokeapi-success-flow", flow.getName());
        assertEquals(3, flow.getSteps().size());
    }

        @Test
    void parsesPartiallyValidFlow() {
        // TODO: create partially valid YAML file
    }
}
