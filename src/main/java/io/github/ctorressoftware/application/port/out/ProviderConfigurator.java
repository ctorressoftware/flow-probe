package io.github.ctorressoftware.application.port.out;

import io.github.ctorressoftware.application.port.in.provider.configure.ProviderStatus;

import java.util.Map;

public interface ProviderConfigurator {
    void configure(Map<String, String> credentials);

    void remove();

    ProviderStatus status();
}
