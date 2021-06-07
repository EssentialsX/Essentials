import net.kyori.indra.git.IndraGitExtension
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.gradle.api.Project

final class GitUtil {
    private GitUtil() {
    }

    static int commitsSinceLastTag(Project project) {
        def indraGit = project.extensions.findByType(IndraGitExtension.class)
        if (indraGit == null || !indraGit.isPresent() || indraGit.tags().isEmpty()) {
            return -1
        }
        def tags = indraGit.tags()
        def depth = 0
        def walk = new RevWalk(indraGit.git().getRepository())
        def commit = walk.parseCommit(indraGit.commit())
        while (true) {
            for (tag in tags) {
                if (walk.parseCommit(tag.getLeaf().getObjectId()) == commit) {
                    walk.dispose()
                    return depth
                }
            }
            depth++
            commit = walk.parseCommit(commit.getParents()[0])
        }
    }

    static String headBranchName(Project project) {
        if (System.getenv("GITHUB_HEAD_REF") != null && !System.getenv("GITHUB_HEAD_REF").isEmpty()) {
            return System.getenv("GITHUB_HEAD_REF")
        } else if (System.getenv("GITHUB_REF") != null && !System.getenv("GITHUB_REF").isEmpty()) {
            return System.getenv("GITHUB_REF").replaceFirst("refs/heads/", "")
        }

        def indraGit = project.extensions.findByType(IndraGitExtension.class)
        if (!indraGit.isPresent()) {
            return "detached-head"
        }

        Ref ref = indraGit.git().getRepository().exactRef('HEAD')?.target
        if (ref == null) {
            return "detached-head"
        }

        return Repository.shortenRefName(ref.name)
    }
}
