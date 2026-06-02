package io.github.ctorressoftware;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class YAMLProcessor {

    public Map<String, Object> read(String filePath) {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("test.yaml");
        Map<String, Object> obj = (Map<String, Object>) yaml.load(inputStream);
        return obj;
    }

}
