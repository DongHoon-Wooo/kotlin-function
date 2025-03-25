plugins {
    kotlin("jvm") version "2.1.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(libs.http4k.bom))
    implementation(libs.http4k.core)
    implementation(libs.http4k.server.jetty)
    implementation(libs.jetty.server)

//    testImplementation("io.strikt:strikt-core:0.34.0")
    testImplementation(libs.strikt.core)
    testImplementation(libs.http4k.client.jetty)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}