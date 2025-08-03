import com.google.gson.GsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.util.regex.Pattern

abstract class VersionDataTask : DefaultTask() {
    @OutputFile
    val destination = project.objects.fileProperty()

    @TaskAction
    fun generateVersionData() {
        val versionUtilFile =
            project.rootProject.file("Essentials/src/main/java/com/earth2me/essentials/utils/VersionUtil.java")
        if (!versionUtilFile.exists()) {
            logger.warn("VersionUtil not found")
            return
        }

        val content = versionUtilFile.readText()
        val supportedVersions = extractSupportedVersions(content)

        if (supportedVersions.isEmpty()) {
            logger.warn("No supported versions found in VersionUtil")
            return
        }

        val versionData = mapOf("supportedVersions" to supportedVersions)
        val json = GsonBuilder().create().toJson(versionData)

        val output = project.file("build/generated/version-data.json")
        output.parentFile.mkdirs()
        output.writeText(json)
        destination.get().asFile.parentFile.mkdirs()
        output.copyTo(destination.get().asFile, overwrite = true)
    }

    private fun extractSupportedVersions(content: String): List<String> {
        val supportedVersions = mutableListOf<String>()

        val supportedVersionsPattern = Pattern.compile(
            "supportedVersions\\s*=\\s*ImmutableSet\\.of\\(([^)]+)\\)",
            Pattern.DOTALL
        )
        val matcher = supportedVersionsPattern.matcher(content)

        if (matcher.find()) {
            val versionsString = matcher.group(1)
            val versionNames = versionsString.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            for (versionName in versionNames) {
                val versionString = extractVersionString(content, versionName)
                if (versionString != null) {
                    supportedVersions.add(versionString)
                }
            }
        }

        return supportedVersions
    }

    private fun extractVersionString(content: String, versionName: String): String? {
        val versionPattern = Pattern.compile(
            "BukkitVersion\\s+$versionName\\s*=\\s*BukkitVersion\\.fromString\\(\"([^\"]+)\"\\)"
        )
        val matcher = versionPattern.matcher(content)

        return if (matcher.find()) {
            matcher.group(1)
        } else {
            null
        }
    }
}
