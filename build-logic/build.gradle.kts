plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("net.kyori", "indra-common", "3.1.3")
    implementation("com.gradleup.shadow", "shadow-gradle-plugin", "8.3.6")
    implementation("xyz.jpenilla", "run-task", "2.3.1")
    implementation("org.yaml", "snakeyaml", "2.4")
    implementation("com.google.code.gson", "gson", "2.12.1")
}
