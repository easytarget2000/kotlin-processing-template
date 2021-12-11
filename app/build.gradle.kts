plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.0"

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(14))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Common location of Processing 4 core libs on macOS:
    implementation(fileTree("/Applications/Processing.app/Contents/Java/core/library/"))

    implementation(project(":clapper", configuration = "default"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClass.set("eu.ezytarget.processing_template.Main")
}
