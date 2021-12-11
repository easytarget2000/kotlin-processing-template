# Kotlin Template for Processing 4

Template project for a Processing 4 Kotlin project using Gradle.

# How To Run

1. Confirm the location of your Processing 4 libraries in `app/build.gradle.kts`:
```kotlin
dependencies {
    // ...
    implementation(files("/Applications/Processing.app/Contents/Java/core/library/core.jar"))
    // ...
} 
```
2. Start the `PApplet` by running the following command from the project root:
```shell
./gradlew run
```
