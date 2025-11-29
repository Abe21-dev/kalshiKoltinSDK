plugins {
    kotlin("jvm") version "2.2.20"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:3.3.2")
    implementation("io.ktor:ktor-client-cio:3.3.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}