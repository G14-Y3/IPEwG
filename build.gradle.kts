import de.undercouch.gradle.tasks.download.Download
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("de.undercouch.download").version("3.4.3")
}

val implementationVersion = "1.0-SNAPSHOT"

group = "group14"
version = implementationVersion

java {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
}

application {
    applicationDefaultJvmArgs = listOf("-Djava.library.path=./lib/libtorch/lib", "-Xms4096m", "-Xmx4096m")
    // mainClass.set("ImageProcessor")
    mainClass.set("ImageProcessorKt") // due to a bug of shadowJar, it will always take this name if exists
    // Besides, ImageProcessorKt is the one I found in ./build/classes/kotlin/main, which works as the executable in JAR
}

repositories {
    mavenLocal()
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
    implementation("net.mahdilamb:colormap:0.9.61")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.31")
    testImplementation("org.testfx:testfx-core:4.0.15-alpha")
    testImplementation("org.testfx:testfx-junit:4.0.15-alpha")
}

tasks.test {
    useJUnit()
    jvmArgs("--add-exports=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED", "-Djava.library.path=./lib/libtorch/lib", "-Xms4096m", "-Xmx4096m")
    dependsOn("extractLibtorch")
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

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "ImageProcessorKt",
            "Implementation-Title" to "IPEwG",
            "Implementation-Version" to archiveVersion,
        )
    }
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("IPEwG")
    archiveClassifier.set("")
    archiveVersion.set(implementationVersion)
    manifest {
        attributes(
            "Main-Class" to "ImageProcessorKt",
            "Implementation-Title" to "IPEwG",
            "Implementation-Version" to archiveVersion,
        )
    }
}

task<Exec>("buildNativeExecutable") {
    dependsOn("shadowJar")

    val mainClassName = "ImageProcessorKt"
    val graalVmHome = System.getenv("GRAAVLVM_HOME")?.ifEmpty { System.getenv("JAVA_HOME") }
        ?: System.getenv("JAVA_HOME")
    val EXE_NAME = "IPEwG"
    val EXE_PATH = "$buildDir/bin/"
    val JAR = "$buildDir/libs/IPEwG-1.0-SNAPSHOT.jar"
    val resourceConfigurationFile = "./resource-config.json"

    // FIX ME: Not sure why it says "unrecognised option --class-path balabala"
    commandLine(
        "$graalVmHome/bin/native-image",
        "--class-path \"$JAR\"",
        "--no-fallback",
        "-Djava.library.path=./lib/libtorch/lib",
        "-H:+StaticExecutable",
        "-H:Class=$mainClassName",
        "-H:Name=$EXE_NAME",
        "-H:Path=$EXE_PATH",
        "-H:ResourceConfigurationFiles=$resourceConfigurationFile",
        "--libc",
        "--static",
        "--target",
        // "-jar \"$JAR\"",
        "--verbose",
    )
}
