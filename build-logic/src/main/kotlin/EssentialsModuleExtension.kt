import org.gradle.api.Project

abstract class EssentialsModuleExtension(private val project: Project) {
    val archiveFile = project.objects.fileProperty()
}
