package io.github.ctorressoftware.infrastructure.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class FlowProbeCommandTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldExecuteRootCommandSuccessfully() {

        FlowProbeCommand command = new FlowProbeCommand();
        CommandLine commandLine = new CommandLine(command);
        int exitCode = commandLine.execute();

        String output = outputStream.toString();

        Assertions.assertEquals(0, exitCode);
        Assertions.assertTrue(output.contains("Usage:"));
        Assertions.assertTrue(output.contains("flowprobe"));
    }
}