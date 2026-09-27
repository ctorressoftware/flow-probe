package io.github.ctorressoftware.infrastructure.cli.provider;

import picocli.CommandLine;

import java.io.InputStream;
import java.util.Properties;

public class VersionProvider implements CommandLine.IVersionProvider {
    @Override
    public String[] getVersion() throws Exception {
        Properties properties = new Properties();

        try (InputStream input = getClass().getResourceAsStream("/version.properties")) {
            if (input == null) {
                return new String[] { "flowprobe version unknown" };
            }
            properties.load(input);
        }

        String projectVersion = properties.getProperty("version", "unknown");
        return new String[] { "flowprobe " + projectVersion };
    }
}