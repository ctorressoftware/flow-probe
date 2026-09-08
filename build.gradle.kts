plugins {
    id("java")
    id("application")
    id("org.graalvm.buildtools.native") version "1.1.9"
    id("jacoco")
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

group = "io.github.ctorressoftware"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("info.picocli:picocli:4.7.7")
    annotationProcessor("info.picocli:picocli-codegen:4.7.7")
    implementation("org.yaml:snakeyaml:2.6")
    implementation("com.github.javakeyring:java-keyring:1.0.4")
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.22.2"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation(platform("org.junit:junit-bom:5.14.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

jacoco {
    toolVersion = "0.8.15"
}

tasks.test {
    useJUnitPlatform{
        excludeTags("os-keystore")
    }

    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    description = "Generate Jacoco coverage reports after running tests."
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("**/generated/**")
                exclude("io/github/ctorressoftware/Main.class")
            }
        })
    )
    reports {
        xml.required = true
        csv.required = false
        html.required = true
        html.outputLocation = layout.buildDirectory.dir("reports/jacoco")
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("**/generated/**")
                exclude("io/github/ctorressoftware/Main.class")
            }
        })
    )
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.60".toBigDecimal()
            }

            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.40".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
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

tasks.register<Test>("osKeystoreTest") {
    group = "verification"
    description = "Runs integration tests against the operating system keystore."

    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath

    useJUnitPlatform {
        includeTags("os-keystore")
    }
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