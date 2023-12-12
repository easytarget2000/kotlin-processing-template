import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application
}

group = "eu.ezytarget.processingtemplate"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(fileTree("/Applications/Processing.app/Contents/Java/core/library/"))
    implementation(fileTree("/Users/mitch/Documents/Processing/libraries/video/library/"))
    implementation(fileTree("/Users/mitch/Documents/Processing/libraries/themidibus/library/"))

    implementation(project(":clapper", configuration = "default"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClass.set("eu.ezytarget.processingtemplate.Main")
}
