plugins {
    `kotlin-dsl`
    `groovy-gradle-plugin`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("net.kyori", "indra-common", "2.0.6")
    implementation("gradle.plugin.com.github.jengelman.gradle.plugins", "shadow", "7.0.0")
    implementation("xyz.jpenilla", "run-paper", "1.0.4")
}
