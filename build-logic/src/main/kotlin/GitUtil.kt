import net.kyori.indra.git.IndraGitExtension
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

object GitUtil {
    @JvmStatic
    fun commitsSinceLastTag(project: Project): Int? {
        val indraGit = project.extensions.findByType(IndraGitExtension::class)?.takeIf {
            it.isPresent && it.tags().isNotEmpty()
        } ?: return null
        val git = indraGit.git() ?: return null

        val tags = indraGit.tags()
        var depth = 0
        val walk = RevWalk(git.repository)
        var commit = walk.parseCommit(indraGit.commit())
        while (true) {
            for (tag in tags) {
                if (walk.parseCommit(tag.leaf.objectId) == commit) {
                    walk.dispose()
                    return depth
                }
            }
            depth++
            commit = walk.parseCommit(commit.parents[0])
        }
    }

    @JvmStatic
    fun headBranchName(project: Project): String {
        System.getenv("GITHUB_HEAD_REF")?.takeIf { it.isNotEmpty() }
                ?.let { return it }
        System.getenv("GITHUB_REF")?.takeIf { it.isNotEmpty() }
                ?.let { return it.replaceFirst("refs/heads/", "") }

        val indraGit = project.extensions.findByType(IndraGitExtension::class)
                ?.takeIf { it.isPresent }

        val ref = indraGit?.git()?.repository?.exactRef("HEAD")?.target
                ?: return "detached-head"

        return Repository.shortenRefName(ref.name)
    }
}