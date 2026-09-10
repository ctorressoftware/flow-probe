# Changelog

All notable changes to FlowProbe are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project follows semantic versioning conventions for its release identifiers.

## [0.1.0-rc.1] - 2026-09-10

First public release candidate.

### Added

- YAML-defined, ordered multi-step HTTP flow execution.
- `run --file <path>` CLI command for executing a flow.
- `configure azure` CLI command for Azure DevOps provider configuration.
- HTTP status expectations with exact status matching.
- Default success behavior accepting any `2xx` response when no explicit status is configured.
- JSON body expectations using JSON Pointer paths.
- `equals` and `notEquals` expectation operators.
- JSON response exports that can be reused by later steps.
- Typed JSON extraction preserving strings, numbers, booleans, objects, arrays, and `null`.
- Placeholder resolution in URLs, HTTP methods, headers, object keys, nested request bodies, and lists.
- Type preservation when a request-body value consists entirely of a placeholder.
- Fail-fast flow execution after the first failed step.
- Reproducible cURL rendering for executed HTTP requests.
- Optional Azure DevOps work-item creation after a failed flow through `--create-impediment`.
- Azure DevOps configuration storage through the operating-system credential store using `java-keyring`.
- Azure DevOps REST API 7.1 work-item integration with request timeouts and provider-specific error handling.
- Percent-encoding of dynamic Azure DevOps URI path segments.
- GraalVM Native Image support.
- Native Image reachability metadata for FlowProbe, SnakeYAML mapping, and macOS `java-keyring` integration.
- macOS x64 (Intel) and arm64 (Apple Silicon) native verification through a GitHub Actions matrix covering JVM checks, a temporary Keychain integration test, Native Image compilation, CLI startup, and a real local HTTP flow smoke test.
- Local HTTP end-to-end tests covering typed exports, request-body serialization, expectations, and fail-fast execution.
- Example flows under `examples/`.
- Apache License 2.0, contribution guidelines, and security reporting documentation.

### Changed

- Centralized JSON serialization, deserialization, and extraction behind the `JsonProcessor` port.
- Provider configuration persistence now uses typed provider configuration models instead of generic string maps.
- Request-body placeholder resolution now preserves structured values until the HTTP request mapping layer serializes them.
- Flow execution returns non-zero exit codes for failed flows and runtime errors.
- Azure DevOps integration now separates timeout, I/O, interruption, and unsuccessful API responses.
- Native builds use committed reachability metadata rather than depending on the external GraalVM Reachability Metadata Repository.

### Fixed

- Prevented request bodies from being serialized twice before POST, PUT, PATCH, and other body-carrying requests.
- Fixed native YAML mapping by registering the required SnakeYAML DTO reflection metadata.
- Fixed HTTP request creation when headers are absent or empty.
- Prevented exports from being written after response validation fails.
- Preserved exported primitive JSON types when reused as exact request-body placeholders.
- Fixed missing JSON Pointer extraction behavior so nonexistent export paths fail explicitly.
- Improved Azure DevOps dynamic path handling for spaces and URI-sensitive values.

### Security

- Redacts common sensitive headers from rendered cURL output, including authorization headers, cookies, API keys, access tokens, and AWS security tokens.
- Stores Azure DevOps PAT configuration in the operating-system credential store instead of project-local plain text.
- Azure DevOps work-item descriptions reuse sanitized request rendering so common sensitive headers are not copied into tickets.

