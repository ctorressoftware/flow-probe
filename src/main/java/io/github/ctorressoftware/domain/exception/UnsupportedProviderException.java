package io.github.ctorressoftware.domain.exception;

import io.github.ctorressoftware.application.port.in.provider.configure.Provider;

public class UnsupportedProviderException extends RuntimeException {
    public UnsupportedProviderException(Provider provider) {
        super("Unsupported provider: " + provider.name());
    }
}
