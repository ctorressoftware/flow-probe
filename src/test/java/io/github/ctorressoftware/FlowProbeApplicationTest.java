package io.github.ctorressoftware;

import io.github.ctorressoftware.infrastructure.cli.ExitCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FlowProbeApplicationTest {

    private final FlowProbeApplication application = new FlowProbeApplication();

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
