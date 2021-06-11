plugins {
    id("essentials.module-conventions")
    id("com.github.johnrengelman.shadow")
}

tasks {
    jar {
        archiveClassifier.set("unshaded")
    }
    shadowJar {
        archiveClassifier.set(null)
    }
}

extensions.configure<EssentialsModuleExtension> {
    archiveFile.set(tasks.shadowJar.flatMap { it.archiveFile })
}
