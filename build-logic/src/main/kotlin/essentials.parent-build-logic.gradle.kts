import xyz.jpenilla.runpaper.task.RunServerTask

plugins {
    id("base")
    id("net.kyori.indra.git")
    id("xyz.jpenilla.run-paper")
}

runPaper {
    disablePluginJarDetection()
}

val runModules = (findProperty("runModules") as String?)
    ?.trim()?.split(",") ?: emptySet()

tasks {
    runServer {
        // Enable modules for this task using the runModules property.
        // Ex: './gradlew :runServer -PrunModules=chat,spawn'
        minecraftVersion(RUN_PAPER_MINECRAFT_VERSION)
    }
    register<RunServerTask>("runAll") {
        group = "essentials"
        description = "Run a test server with all EssentialsX modules."
        minecraftVersion(RUN_PAPER_MINECRAFT_VERSION)
    }
    named<Delete>("clean") {
        delete(file("jars"))
    }
}

subprojects {
    afterEvaluate {
        val moduleExt = extensions.findByType<EssentialsModuleExtension>() ?: return@afterEvaluate
        rootProject.tasks.named<RunServerTask>("runAll").configure {
            pluginJars.from(moduleExt.archiveFile)
        }
        if (name == "EssentialsX") {
            rootProject.tasks.runServer.configure {
                pluginJars.from(moduleExt.archiveFile)
            }
            return@afterEvaluate
        }
        for (module in runModules) {
            if (name.contains(module, ignoreCase = true)) {
                rootProject.tasks.runServer.configure {
                    pluginJars.from(moduleExt.archiveFile)
                }
            }
        }
    }
}
