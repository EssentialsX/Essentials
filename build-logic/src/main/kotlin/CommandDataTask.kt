import com.google.gson.GsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.yaml.snakeyaml.Yaml
import java.util.*

abstract class CommandDataTask : DefaultTask() {
    @OutputFile
    val destination = project.objects.fileProperty()
    @OutputFile
    val permissionDestination = project.objects.fileProperty()

    // i promise i will be safe
    @Suppress("UNCHECKED_CAST")
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

        val permissions = data["permissions"] as? Map<String, Map<String, Any>> ?: emptyMap()

        val extractedPermissions = permissions.mapValues { (_, value) ->
            val default = value["default"] ?: "op"
            val description = value["description"] as? String ?: ""
            val children = value["children"] as? Map<String, Any> ?: emptyMap()

            mapOf(
                "default" to default,
                "description" to description,
                "children" to children
            )
        }.toMutableMap()

        if (extractedCommands.isEmpty()) {
            logger.warn("No commands found in plugin.yml for ${project.name}")
        } else {

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
                            "Description" -> extractedCommands[command] =
                                commandData + ("description" to value.toString())

                            "Usage" -> extractedCommands[command] = commandData + ("usage" to value.toString())
                        }
                    } else {
                        val usagesList =
                            commandData["usages"] as MutableList<Map<String, String>> // verbose command usages
                        usagesList.add(
                            mapOf(
                                "usage" to value.toString(),
                                "description" to properties["${command}CommandUsage${index}Description"]?.toString()
                                    .orEmpty()
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
        }

        if (extractedPermissions.isEmpty()) {
            logger.warn("No permissions found in plugin.yml for ${project.name}")
        } else {
            val json = GsonBuilder().create().toJson(extractedPermissions)
            val output = project.file("build/generated/${project.name}-permissions.json")
            output.parentFile.mkdirs()
            output.writeText(json)
            permissionDestination.get().asFile.parentFile.mkdirs()
            output.copyTo(permissionDestination.get().asFile, overwrite = true)
        }
    }
}
