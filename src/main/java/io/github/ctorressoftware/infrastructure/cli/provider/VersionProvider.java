package io.github.ctorressoftware.infrastructure.cli.provider;

import picocli.CommandLine;

import java.util.Properties;

public class VersionProvider implements CommandLine.IVersionProvider {
    @Override
    public String[] getVersion() throws Exception {
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("/version.properties"));
        return new String[] { props.getProperty("version") };
    }
}