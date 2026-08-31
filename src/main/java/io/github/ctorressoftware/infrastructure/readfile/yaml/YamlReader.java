package io.github.ctorressoftware.infrastructure.readfile.yaml;

import io.github.ctorressoftware.application.port.out.FlowFileReader;
import io.github.ctorressoftware.domain.model.*;
import io.github.ctorressoftware.infrastructure.readfile.exception.*;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class YamlReader implements FlowFileReader {
    private final Yaml yaml;
    private final YamlFlowMapper yamlFlowMapper;
    private final YamlFlowValidator yamlFlowValidator;

    public YamlReader() {
        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(YamlFlow.class, loaderOptions);
        this.yaml = new Yaml(constructor);
        this.yamlFlowMapper = new YamlFlowMapper();
        this.yamlFlowValidator = new YamlFlowValidator();
    }

    @Override
    public Flow read(FilePath filePath) {
        YamlFlow yamlFlow = parseFile(filePath);
        yamlFlowValidator.validate(filePath, yamlFlow);
        return yamlFlowMapper.map(yamlFlow);
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
}