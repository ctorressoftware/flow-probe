package io.github.ctorressoftware.infrastructure.provider.azure;

import io.github.ctorressoftware.application.port.out.ProviderPrompt;

import java.util.Map;

public class ProviderPromptFactory {
    String DEFAULT_PROVIDER = "AZURE";
    private final Map<String, ProviderPrompt> providerPrompts;

    public ProviderPromptFactory(Map<String, ProviderPrompt> providerPrompts) {
        this.providerPrompts = providerPrompts;
    }

    public ProviderPrompt getPromptByProvider(String provider) {
        return providerPrompts.getOrDefault(
                provider,
                providerPrompts.get(DEFAULT_PROVIDER)
        );
    }
}
