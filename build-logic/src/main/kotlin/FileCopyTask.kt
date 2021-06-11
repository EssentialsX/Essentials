import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class FileCopyTask : DefaultTask() {
  @InputFile
  val fileToCopy = project.objects.fileProperty()

  @OutputFile
  val destination = project.objects.fileProperty()

  @TaskAction
  private fun copyFile() {
    destination.get().asFile.parentFile.mkdirs()
    fileToCopy.get().asFile.copyTo(destination.get().asFile, overwrite = true)
  }
}
