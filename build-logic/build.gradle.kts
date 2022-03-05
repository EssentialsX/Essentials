plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("net.kyori", "indra-common", "2.1.1")
    implementation("gradle.plugin.com.github.johnrengelman", "shadow", "7.1.2")
    implementation("xyz.jpenilla", "run-paper", "1.0.6")
}
