# Native Image

FlowProbe supports GraalVM Native Image and publishes native macOS packages for:

- x64 / Intel
- arm64 / Apple Silicon

## Build locally

Use GraalVM for JDK 21, then run:

```bash
./gradlew nativeCompile
```

The executable is generated at:

```text
build/native/nativeCompile/flowprobe
```

Basic verification:

```bash
./build/native/nativeCompile/flowprobe --version
./build/native/nativeCompile/flowprobe --help
```

## Committed reachability metadata

FlowProbe keeps its Native Image reachability metadata under:

```text
src/main/resources/META-INF/native-image/io.github.ctorressoftware/flow-probe/
```

The directory currently contains metadata for reflection, resources, serialization, proxies, JNI, and predefined classes.

The external GraalVM Reachability Metadata Repository is disabled for the native build; FlowProbe uses its committed metadata.

## Tracing agent filters

Tracing filters live under:

```text
native-image/filters/
├── access-filter.json
└── caller-filter.json
```

The agent uses built-in caller/heuristic filtering plus FlowProbe's custom filters to avoid collecting irrelevant runtime metadata.

## Incremental tracing

The Gradle Native Image agent is configured to merge newly observed metadata directly into the committed FlowProbe metadata directory.

The agent is restricted to the Gradle `run` task.

Example:

```bash
./gradlew -Pagent=direct run \
  --args="run --file examples/normal-flow.yaml"
```

For an interactive provider path:

```bash
./gradlew -Pagent=direct run \
  --args="configure azure"
```

Because metadata is merged into source resources, always inspect what changed:

```bash
git diff -- src/main/resources/META-INF/native-image
```

Do not treat tracing output as automatically trustworthy. A tracing session can observe environment-specific or irrelevant runtime behavior.

## When to use the tracing agent

Do not run the tracing agent merely because a new Java class was added.

Use it when a change introduces or exercises dynamic behavior that Native Image may not discover through static analysis or generated library metadata, such as:

- reflection;
- serialization/deserialization requiring runtime metadata;
- JNI;
- dynamic proxies;
- runtime resource lookup;
- native integrations.

The decisive test is the native executable itself.

## Recommended validation after metadata-sensitive changes

```bash
./gradlew clean check
./gradlew nativeCompile
```

Then exercise the affected native path directly.

For example:

```bash
./build/native/nativeCompile/flowprobe --version
./build/native/nativeCompile/flowprobe run -f examples/normal-flow.yaml
./build/native/nativeCompile/flowprobe configure azure
```

## Version resource

The Gradle project version is the source used to generate:

```text
version.properties
```

`VersionProvider` reads that resource for `flowprobe --version`.

The native binary configuration explicitly includes `version.properties` as a Native Image resource.

## Release verification

The tagged macOS release workflow:

1. verifies that the pushed tag matches the Gradle application version;
2. runs `clean check`;
3. creates a temporary macOS Keychain;
4. runs the OS keystore integration test;
5. compiles the Native Image executable;
6. smoke-tests the native CLI;
7. executes a real local HTTP flow against a temporary server;
8. packages Intel and Apple Silicon artifacts;
9. publishes the GitHub release;
10. calculates SHA256 values and updates the Homebrew tap.
