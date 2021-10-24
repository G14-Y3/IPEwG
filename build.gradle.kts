import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    application
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group = "group14"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("ImageProcessor")
    val LIBTORCH_HOME = System.getenv("LIBTORCH_HOME")
    applicationDefaultJvmArgs = listOf("-Djava.library.path=${LIBTORCH_HOME}/lib")
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
//    implementation("com.facebook.fbjni:fbjni-java-only:0.2.2")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}