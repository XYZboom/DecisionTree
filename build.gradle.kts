plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.github.xyzboom"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    //graphviz and its dependencies
    implementation("guru.nidi:graphviz-kotlin:0.18.1")
    implementation("guru.nidi:graphviz-java-all-j2v8:0.18.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}