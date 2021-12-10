import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    id("net.kyori.indra")
    id("net.kyori.indra.checkstyle")
    id("net.kyori.indra.publishing")
}

val baseExtension = extensions.create<EssentialsBaseExtension>("essentials", project)

val checkstyleVersion = "8.36.2"
val spigotVersion = "1.18.1-R0.1-SNAPSHOT"
val junit5Version = "5.7.0"
val mockitoVersion = "3.2.0"

dependencies {
    testImplementation("org.junit.jupiter", "junit-jupiter", junit5Version)
    testImplementation("org.junit.vintage", "junit-vintage-engine", junit5Version)
    testImplementation("org.mockito", "mockito-core", mockitoVersion)

    constraints {
        implementation("org.yaml:snakeyaml:1.28") {
            because("Bukkit API ships old versions, Configurate requires modern versions")
        }
    }
}

afterEvaluate {
    if (baseExtension.injectBukkitApi.get()) {
        dependencies {
            api("org.spigotmc", "spigot-api", spigotVersion)
        }
    }
    if (baseExtension.injectBstats.get()) {
        dependencies {
            implementation("org.bstats", "bstats-bukkit", "1.8")
        }
    }
}

tasks {
    // Version Injection
    processResources {
        // Always process resources if version string or git branch changes
        val fullVersion = rootProject.ext["FULL_VERSION"] as String
        val gitBranch = rootProject.ext["GIT_BRANCH"] as String
        inputs.property("fullVersion", fullVersion)
        inputs.property("gitBranch", gitBranch)
        filter<ReplaceTokens>(
            "beginToken" to "\${",
            "endToken" to "}",
            "tokens" to mapOf(
                "full.version" to fullVersion,
                "git.branch" to gitBranch
            )
        )
    }
    compileJava {
        options.compilerArgs.add("-Xlint:-deprecation")
    }
    javadoc {
        title = "${project.name} API (v${rootProject.ext["FULL_VERSION"]})"
        val options = options as? StandardJavadocDocletOptions ?: return@javadoc
        options.links(
            "https://hub.spigotmc.org/javadocs/spigot/"
        )
        options.addBooleanOption("Xdoclint:none", true)
    }
    withType<Jar> {
        archiveVersion.set(rootProject.ext["FULL_VERSION"] as String)
    }
    withType<Sign> {
        onlyIf { project.hasProperty("forceSign") }
    }
}

// Dependency caching
configurations.all {
    resolutionStrategy.cacheChangingModulesFor(5, "minutes")
}

indra {
    checkstyle(checkstyleVersion)

    github("EssentialsX", "Essentials")
    gpl3OnlyLicense()

    publishReleasesTo("essx", "https://repo.essentialsx.net/releases/")
    publishSnapshotsTo("essx", "https://repo.essentialsx.net/snapshots/")

    configurePublications {
        pom {
            description.set("The essential plugin suite for Minecraft servers.")
            url.set("https://essentialsx.net")
            developers {
                developer {
                    id.set("mdcfe")
                    name.set("MD")
                    email.set("md@n3fs.co.uk")
                }
                developer {
                    id.set("pop4959")
                }
                developer {
                    id.set("JRoy")
                    name.set("Josh Roy")
                }
            }
            ciManagement {
                system.set("Jenkins")
                url.set("https://ci.ender.zone/job/EssentialsX")
            }
        }
    }

    javaVersions {
        target(8)
        minimumToolchain(17)
    }
}

// undo https://github.com/KyoriPowered/indra/blob/master/indra-common/src/main/kotlin/net/kyori/indra/IndraPlugin.kt#L57
convention.getPlugin<BasePluginConvention>().archivesBaseName = project.name
