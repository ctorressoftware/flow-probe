package io.github.ctorressoftware.integration.keystore;

import com.github.javakeyring.Keyring;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@Tag("os-keystore")
class OsKeystoreIntegrationTest {

    @Test
    void shouldStoreReadAndDeleteCredentialUsingOperatingSystemKeystore() throws Exception {

        String domain = "flowprobe-ci";
        String account = "test-" + UUID.randomUUID();
        String secret = "dummy-secret";

        try (Keyring keyring = Keyring.create()) {

            try {
                keyring.setPassword(domain, account, secret);
                Assertions.assertEquals(secret, keyring.getPassword(domain, account));
            } finally {
                keyring.deletePassword(domain, account);
            }
        }
    }
}
