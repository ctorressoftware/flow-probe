# Development guide

This document covers local verification, tests, Native Image maintenance, and the tagged release workflow.

For contribution etiquette and pull-request guidance, see [`CONTRIBUTING.md`](../CONTRIBUTING.md).

## Requirements

For JVM development:

- Java 21
- Gradle Wrapper

For Native Image:

- GraalVM for JDK 21
- Native Image support in the selected GraalVM distribution

## Run the application

Help:

```bash
./gradlew run --args="--help"
```

Version:

```bash
./gradlew run --args="--version"
```

Run an example:

```bash
./gradlew run --args="run -f examples/normal-flow.yaml"
```

The Gradle `run` task is configured to forward standard input because provider configuration is interactive.

## Unit and integration tests

Run the standard test suite:

```bash
./gradlew test
```

The normal `test` task excludes tests tagged:

```text
os-keystore
```

Run the operating-system keystore integration test separately:

```bash
./gradlew osKeystoreTest
```

## Full JVM verification

Before a release or pull request:

```bash
./gradlew clean check
```

`clean` removes generated Gradle output under `build/`.

The committed Native Image metadata under `src/main/resources/META-INF/native-image` is not removed by `clean`.

`check` includes the standard tests and JaCoCo coverage verification.

Current coverage thresholds:

```text
LINE   >= 60%
BRANCH >= 40%
```

## JaCoCo reports

The test task finalizes with the JaCoCo report.

HTML reports are generated under:

```text
build/reports/jacoco
```

## Native build

```bash
./gradlew nativeCompile
```

Generated executable:

```text
build/native/nativeCompile/flowprobe
```

For a completely fresh native build:

```bash
./gradlew clean nativeCompile
```

Remember that `clean` removes the previously generated binary because it lives under `build/`.

## Native smoke checks

Useful checks:

```bash
./build/native/nativeCompile/flowprobe --version
./build/native/nativeCompile/flowprobe --help
./build/native/nativeCompile/flowprobe run --help
./build/native/nativeCompile/flowprobe configure --help
./build/native/nativeCompile/flowprobe run -f examples/normal-flow.yaml
```

An intentionally unsupported provider is useful for validating CLI argument handling:

```bash
./build/native/nativeCompile/flowprobe configure jira
echo $?
```

The expected exit code for invalid CLI arguments is `2`.

## Native Image metadata

See [Native Image](native-image.md) for the detailed tracing workflow.

Metadata is committed under:

```text
src/main/resources/META-INF/native-image/io.github.ctorressoftware/flow-probe
```

The tracing agent merges new observations directly into that directory.

After tracing:

```bash
git diff -- src/main/resources/META-INF/native-image
```

Review every metadata change before committing it.

## Versioning

The project version is declared in:

```text
build.gradle.kts
```

The build generates a `version.properties` resource from that value.

The CLI version provider reads the generated resource, which avoids maintaining a separate hardcoded version in the Picocli command.

## Release workflow

The release workflow is:

```text
.github/workflows/macos-native-release.yml
```

It runs when a tag matching `v*` is pushed.

Before creating a release, it validates that:

```text
tag == "v" + Gradle project version
```

For example:

```text
Gradle version: 0.1.0-rc.4
Tag:            v0.1.0-rc.4
```

The workflow builds both macOS architectures:

- x64 / Intel
- arm64 / Apple Silicon

It then publishes the GitHub release and updates the `ctorressoftware/homebrew-tap` formula with the generated artifact URLs and SHA256 values.

## Release preparation checklist

Before pushing a release tag:

```bash
./gradlew clean check
./gradlew nativeCompile
```

Verify the native CLI and the behavior changed by the release.

Then confirm:

```bash
git status
git log -5 --oneline
```

The working tree should be clean.

Create and push the annotated tag:

```bash
git tag -a v<version> -m "FlowProbe v<version>"
git push origin v<version>

Example: v0.1.0-rc.4
```

For a stable release, use the corresponding stable version/tag instead.

## Homebrew verification

After the tagged workflow updates the tap:

```bash
brew update
brew upgrade ctorressoftware/tap/flowprobe
flowprobe --version
```

If necessary during local troubleshooting:

```bash
brew reinstall ctorressoftware/tap/flowprobe
```
