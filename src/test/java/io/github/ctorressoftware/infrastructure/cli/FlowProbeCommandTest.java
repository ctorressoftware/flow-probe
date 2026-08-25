package io.github.ctorressoftware.infrastructure.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void shouldPrintUsageWhenExecutedWithoutSubcommand() {

        FlowProbeCommand command = new FlowProbeCommand();

        command.run();
        String output = outputStream.toString();

        Assertions.assertTrue(output.contains("Usage:"));
        Assertions.assertTrue(output.contains("flowprobe"));
        Assertions.assertTrue(output.contains("CLI tool to execute and verify HTTP flows"));
    }
}