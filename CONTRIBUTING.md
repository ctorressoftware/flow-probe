# Contributing to FlowProbe

Thanks for your interest in contributing to FlowProbe.

## Requirements

- Java 21
- Gradle Wrapper

## Build

```bash
./gradlew build
```

## Run Tests

```bash
./gradlew test
```

## Full Verification

Before submitting a pull request, run:

```bash
./gradlew clean check
```

## Pull Requests

Before opening a pull request:

1. Keep changes focused and small when possible.
2. Add or update tests when behavior changes.
3. Ensure `./gradlew clean check` passes.
4. Avoid unrelated formatting or refactors.
5. Describe clearly what changed and why.
6. Do not include credentials, tokens, PATs, API keys, or other secrets.

## Bug Reports

When reporting a bug, include:

- FlowProbe version
- Operating system
- Java version, when relevant
- YAML flow or minimal reproduction when applicable
- Expected behavior
- Actual behavior
- Relevant error output or logs

Never include credentials, tokens, PATs, API keys, cookies, or other sensitive information.

## Feature Requests

Feature requests are welcome.

Please describe:

- The problem you are trying to solve
- The expected behavior
- Why the feature would be useful to FlowProbe users

Whenever possible, focus on the use case rather than a specific implementation.

## Code Style

Follow the existing project structure and conventions.

Prefer:

- Clear and explicit code
- Small, cohesive classes
- Meaningful names
- Tests that validate behavior rather than implementation details
- Minimal dependencies

Avoid unnecessary abstractions or refactors unrelated to the contribution.

## Commit Messages

FlowProbe generally follows Conventional Commit-style messages.

Examples:

```text
feat: add response expectation validation
fix: preserve exported JSON value types
test: add request mapper regression tests
refactor: simplify placeholder resolution
docs: update contributing guide
```

## Security

Do not report security vulnerabilities through public GitHub issues.

See [SECURITY.md](SECURITY.md) for information about reporting security issues privately.