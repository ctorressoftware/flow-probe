package io.github.ctorressoftware;

import picocli.CommandLine;

import java.util.Map;

@CommandLine.Command(name = "FlowProbe", version = "FlowProbe 1.0", mixinStandardHelpOptions = true)
public class CommandManager implements Runnable {

    @CommandLine.Option(
            names = {"-f", "-file"},
            required = false,
            paramLabel = "FILE",
            description = "file url"
    )
    String filePath;

    @Override
    public void run() {
        YAMLProcessor yamlProcessor = new YAMLProcessor();
        Map<String, Object> doc = yamlProcessor.read(filePath);
        System.out.println(doc.keySet());
        System.out.println(doc.values());
    }
}