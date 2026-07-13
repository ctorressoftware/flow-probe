plugins {
    id("java")
    id("application")
    id("org.graalvm.buildtools.native") version "0.10.6"
}

group = "io.github.ctorressoftware"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("info.picocli:picocli:4.7.7")
    annotationProcessor("info.picocli:picocli-codegen:4.7.7")
    implementation("org.yaml:snakeyaml:2.6")
    implementation("com.github.javakeyring:java-keyring:1.0.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    doNotTrackState("FlowProbe CLI must execute on every invocation")
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Aproject=io.github.ctorressoftware/flow-probe")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "io.github.ctorressoftware.Main"
    }
}

tasks.register<JavaExec>("runWithNativeAgent") {
    group = "native"
    description = "Runs FlowProbe with GraalVM native-image agent"

    mainClass.set("io.github.ctorressoftware.Main")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`

    doNotTrackState("Native image tracing must execute on every invocation")

    jvmArgs(
        "-agentlib:native-image-agent=config-merge-dir=src/main/resources/META-INF/native-image/io.github.ctorressoftware/flow-probe"
    )

    args = providers.gradleProperty("appArgs")
        .map { it.split(" ") }
        .getOrElse(listOf("--help"))
}

application {
    mainClass.set("io.github.ctorressoftware.Main")
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("flowprobe")
            mainClass.set("io.github.ctorressoftware.Main")
        }
    }
}