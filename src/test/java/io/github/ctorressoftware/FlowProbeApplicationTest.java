package io.github.ctorressoftware;

import io.github.ctorressoftware.infrastructure.cli.ExitCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class FlowProbeApplicationTest {

    private final FlowProbeApplication application = new FlowProbeApplication();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void shouldReturnSuccessWhenHelpIsRequested() {

        int exitCode = application.run(new String[]{"--help"});
        Assertions.assertEquals(
                ExitCode.SUCCESS.code(),
                exitCode
        );
    }

    @Test
    void shouldReturnInvalidArgumentsWhenRunFileIsMissing() {

        int exitCode = application.run(new String[]{"run"});
        Assertions.assertEquals(
                ExitCode.INVALID_ARGUMENTS.code(),
                exitCode
        );
    }
}
