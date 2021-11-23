import de.undercouch.gradle.tasks.download.Download
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
    application
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("de.undercouch.download").version("3.4.3")
}

group = "group14"
version = "1.0-SNAPSHOT"

application {
    applicationDefaultJvmArgs = listOf("-Djava.library.path=./lib/libtorch/lib")
    mainClass.set("ImageProcessor")
}

repositories {
    mavenCentral()
}

javafx {
    version = "14"
    modules = listOf("javafx.controls", "javafx.graphics", "javafx.swing")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.0")
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("org.pytorch:pytorch_java_only:1.9.0")
    implementation("com.facebook.soloader:nativeloader:0.10.1")
    implementation("com.facebook.fbjni:fbjni-java-only:0.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    implementation("org.tensorflow:tensorflow-core-platform:0.3.3")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.31")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    dependsOn("extractLibtorch")
}

task<Download>("downloadLibtorch") {
    val src = if (Os.isFamily(Os.FAMILY_WINDOWS)) {
        "https://download.pytorch.org/libtorch/cpu/libtorch-win-shared-with-deps-1.10.0%2Bcpu.zip"
    } else if (Os.isFamily(Os.FAMILY_MAC)) {
        "https://download.pytorch.org/libtorch/cpu/libtorch-macos-1.10.0.zip"
    } else if (Os.isFamily(Os.FAMILY_UNIX)) {
        "https://download.pytorch.org/libtorch/cpu/libtorch-shared-with-deps-1.10.0%2Bcpu.zip"
    } else {
        throw GradleException("Unsupported platform")
    }

    overwrite(false)

    src(src)
    dest("lib/libtorch-dist.zip")
}

task<Copy>("extractLibtorch") {
    from(zipTree("lib/libtorch-dist.zip"))
    into("lib/")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn("downloadLibtorch")
}
