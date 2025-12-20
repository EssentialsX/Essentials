plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("net.kyori:indra-common:3.2.0")
    implementation("com.gradleup.shadow:shadow-gradle-plugin:9.1.0")
    implementation("xyz.jpenilla:run-task:3.0.0")
    implementation("org.yaml:snakeyaml:2.4")
    implementation("com.google.code.gson:gson:2.13.2")
}
