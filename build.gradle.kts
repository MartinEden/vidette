plugins {
    kotlin("jvm") version "2.3.20"
    application
    distribution
}

group = "eden"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:5.1.0")
    testImplementation(kotlin("test"))
}

application {
    mainClass = "eden.vidette.MainKt"
    // https://teamdev.com/jxbrowser/blog/native-access-restrictions-in-java-24/
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}