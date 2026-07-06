package io.github.ctorressoftware.infrastructure.cli;

import java.util.concurrent.Callable;

public class LoginCommand implements Callable<Integer> {

    public LoginCommand() {}

    @Override
    public Integer call() {
        // TODO: to make azure-properties configuration
        return 0;
    }
}

