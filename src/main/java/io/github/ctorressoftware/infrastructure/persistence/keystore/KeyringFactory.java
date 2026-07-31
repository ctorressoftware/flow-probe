package io.github.ctorressoftware.infrastructure.persistence.keystore;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;

@FunctionalInterface
public interface KeyringFactory {
    Keyring create() throws BackendNotSupportedException;
}
