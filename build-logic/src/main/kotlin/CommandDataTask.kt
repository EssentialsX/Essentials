import com.google.gson.GsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.yaml.snakeyaml.Yaml
import java.util.*

abstract class CommandDataTask : DefaultTask() {
    @OutputFile
    val destination = project.objects.fileProperty()

    @TaskAction
    private fun harvest() {
        val pluginYml = project.file("src/main/resources/plugin.yml")
        if (!pluginYml.exists()) {
            logger.warn("No plugin.yml found to harvest")
            return
        }

        val messagesProps = project.rootProject.file("Essentials/src/main/resources/messages.properties")
        if (!messagesProps.exists()) {
            logger.warn("No messages.properties found to harvest")
            return
        }

        val yaml = Yaml()
        val data: Map<String, Any> = yaml.load(pluginYml.inputStream())

        // i promise i will be safe
        @Suppress("UNCHECKED_CAST")
        val commands = data["commands"] as? Map<String, Map<String, Any>> ?: emptyMap()

        val extractedCommands = commands.mapValues { (_, details) ->
            val aliases = when (val aliasesData = details["aliases"]) {
                is String -> listOf(aliasesData)
                is List<*> -> aliasesData.filterIsInstance<String>()
                else -> emptyList()
            }

            mapOf(
                "aliases" to aliases,
                "description" to "",
                "usage" to "",
                "usages" to mutableListOf<Map<String, String>>()
            )
        }.toMutableMap()

        if (extractedCommands.isEmpty()) {
            logger.warn("No commands found in plugin.yml for ${project.name}")
            return
        }

        val properties = Properties()
        messagesProps.inputStream().use { properties.load(it) }

        properties.forEach { key, value ->
            val commandKeyRegex = Regex("^(\\w+)Command(Description|Usage)(\\d*)$")
            val match = commandKeyRegex.matchEntire(key.toString())

            if (match != null) {
                val (command, type, index) = match.destructured
                val commandData = extractedCommands[command] ?: return@forEach

                if (index.isEmpty()) {
                    // main description and usage
                    when (type) {
                        "Description" -> extractedCommands[command] = commandData + ("description" to value.toString())
                        "Usage" -> extractedCommands[command] = commandData + ("usage" to value.toString())
                    }
                } else {
                    @Suppress("UNCHECKED_CAST")
                    val usagesList = commandData["usages"] as MutableList<Map<String, String>> // verbose command usages
                    usagesList.add(
                        mapOf(
                            "usage" to value.toString(),
                            "description" to properties["${command}CommandUsage${index}Description"]?.toString().orEmpty()
                        )
                    )
                }
            }
        }

        val json = GsonBuilder().create().toJson(extractedCommands)
        val output = project.file("build/generated/${project.name}-commands.json")
        output.parentFile.mkdirs()
        output.writeText(json)
        destination.get().asFile.parentFile.mkdirs()
        output.copyTo(destination.get().asFile, overwrite = true)

        val sourceDir = project.file("src/main/java")
        if (!sourceDir.exists() || !sourceDir.isDirectory) {
            logger.warn("No Java source directory found at src/main/java; skipping isAuthorized scanning.")
            return
        }

        val authCallRegex = Regex("""\.\s*isAuthorized\s*\(\s*"([^"]+)""")
        val permissionsFound = mutableSetOf<String>()

        sourceDir.walkTopDown().filter { it.isFile && it.extension == "java" }.forEach { file ->
            file.forEachLine { line ->
                authCallRegex.findAll(line).forEach { matchResult ->
                    val permission = matchResult.groupValues[1]
                    if (permission.isNotBlank()) {
                        permissionsFound.add(permission)
                    }
                }
            }
        }

        val authJson = GsonBuilder().create().toJson(permissionsFound)
        val authOutputFile = project.file("build/generated/${project.name}-permissions.json")
        authOutputFile.parentFile.mkdirs()
        authOutputFile.writeText(authJson)
        destination.get().asFile.parentFile.mkdirs()
        authOutputFile.copyTo(destination.get().asFile, overwrite = true)
    }
}
