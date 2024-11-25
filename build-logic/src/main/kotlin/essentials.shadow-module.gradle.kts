plugins {
    id("essentials.module-conventions")
    id("com.gradleup.shadow")
}

tasks {
    jar {
        archiveClassifier.set("unshaded")
    }
    shadowJar {
        archiveClassifier.set(null as String?)
    }
}

extensions.configure<EssentialsModuleExtension> {
    archiveFile.set(tasks.shadowJar.flatMap { it.archiveFile })
}
