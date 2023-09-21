plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("net.kyori", "indra-common", "3.1.1")
    implementation("com.github.johnrengelman", "shadow", "8.1.1")
    implementation("xyz.jpenilla", "run-task", "2.1.0")
    implementation("org.gradlex:java-ecosystem-capabilities:1.3.1")
}
