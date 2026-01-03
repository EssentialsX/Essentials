dependencyResolutionManagement {
    repositories {
        maven("https://maven-prs.papermc.io/Paper/pr13194") {
            name = "Maven for PR #13194" // https://github.com/PaperMC/Paper/pull/13194
            mavenContent {
                includeModule("io.papermc.paper", "paper-api")
            }
        }
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/") {
            content {
                includeGroup("org.spigotmc")
                includeGroup("net.md_5")
            }
        }
        maven("https://jitpack.io") {
            content {
                includeGroup("com.github.milkbowl")
                includeGroup("com.github.MinnDevelopment")
            }
        }
        maven("https://repo.codemc.org/repository/maven-public") {
            content { includeGroup("org.bstats") }
        }
        maven("https://repo.helpch.at/releases/") {
            content { includeGroup("me.clip") }
        }
        maven("https://libraries.minecraft.net/") {
            content { includeGroup("com.mojang") }
        }
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "EssentialsXParent"

// Modules
sequenceOf(
    "",
    "AntiBuild",
    "Chat",
    "Discord",
    "DiscordLink",
    "GeoIP",
    "Protect",
    "Spawn",
    "XMPP",
).forEach {
    include(":EssentialsX$it")
    project(":EssentialsX$it").projectDir = file("Essentials$it")
}

// Providers
include(":providers:BaseProviders")
include(":providers:NMSReflectionProvider")
include(":providers:PaperProvider")
include(":providers:1_8Provider")
include(":providers:1_12Provider")
