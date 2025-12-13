plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion = "3.3.2"

dependencies {

    // ktor imports
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")

    // logging and crypto
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.80")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")

    // testing
    testImplementation("io.mockk:mockk:1.14.6")
    testImplementation("io.ktor:ktor-client-mock:${ktorVersion}")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}