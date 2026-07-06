package io.github.ctorressoftware.application.port.out;

import io.github.ctorressoftware.application.port.in.provider.configure.ProviderStatus;

public interface ProviderConfigurator {
    void configure();

    void remove();

    ProviderStatus status();
}
