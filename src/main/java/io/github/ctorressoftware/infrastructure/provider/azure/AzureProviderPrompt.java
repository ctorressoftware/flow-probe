package io.github.ctorressoftware.infrastructure.provider.azure;

import io.github.ctorressoftware.application.port.out.ProviderPrompt;

import java.util.Map;
import java.util.Scanner;

public class AzureProviderPrompt implements ProviderPrompt {

    private final Scanner scanner;

    public AzureProviderPrompt(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public Map<String, String> prompt() {
        return Map.of(
                "Organization", "cetorres",
                "Project", "cetorres",
                "PAT", "wjdhuwhdu378ry3rfbcncjwdnu3"
        );
    }
}
