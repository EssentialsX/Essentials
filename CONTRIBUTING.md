# Contributing to EssentialsX

*By contributing to EssentialsX, you agree to license your changes under the [GNU General Public License version 3](https://github.com/EssentialsX/Essentials/blob/2.x/LICENSE).*

## Submitting code changes to EssentialsX

If you're interested in submitting new code to EssentialsX, we accept changes via GitHub pull requests.

### Adding features

In general, we will only consider PRs for features if they have already been discussed with the team through
GitHub issues and discussions. Check the list of
["open to PR" issues](https://github.com/EssentialsX/Essentials/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc+label%3A%22status%3A+open+to+PR%22)
and [feature request discussions](https://github.com/EssentialsX/Essentials/discussions/categories/ideas-and-feature-suggestions)
before you start working on your changes. If you don't see your desired feature listed,
[open a feature request](https://github.com/EssentialsX/Essentials/issues/new/choose) and wait for a response, otherwise
you may end up wasting your time on a feature that we aren't in a position to accept.

#### Keep it focused

Please try to keep feature PRs focused around one feature. Your PR should ideally contain
one feature, or a few closely-linked features. If you submit several unrelated features 
in one PR, the PR will not be accepted.

### Fixing bugs

If you're opening a PR to fix a bug, please ensure a bug report has been filed - search the
[issue tracker](https://github.com/EssentialsX/Essentials/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc+label%3A%22bug%3A+confirmed%22%2C%22bug%3A+unconfirmed%22%2C%22bug%3A+upstream%22)
for an existing report, and if you can't find a bug report,
[create one](https://github.com/EssentialsX/Essentials/issues/new/choose) before you submit your PR.

#### Unconfirmed bugs

You may find a bug report with the label `bug: unconfirmed`. This means the EssentialsX team hasn't had the time
to review the bug report yet or hasn't been able to confirm
whether the reported issue is actually a bug in EssentialsX. You can help us by following the steps in the report and
posting whether you are able to replicate this issue, ideally on the latest versions of EssentialsX and Paper. This will
help us confirm issues and prioritise PRs accordingly.

#### Upstream bugs

You may also find bug reports with the `bug: upstream` label. This label means the issue is caused by a bug in *another
project*, not EssentialsX. This includes bugs in CraftBukkit/Spigot and bugs in other plugins. It may be appropriate to
mitigate an issue in EssentialsX, but please check with us in the [EssentialsX Development Discord server](https://discord.gg/CUN7qVb) before you submit a PR.

### Making your changes

You'll need the following to make your changes to EssentialsX:

* A GitHub account
* [`git`](https://git-scm.com/downloads): the Git command-line tool, used to track and save your changes
* [`gh`](https://cli.github.com/): the GitHub command-line tool, used to submit your changes to EssentialsX
* Your IDE of choice - we recommend [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download)

If you're already familiar with Git, you can skip this next section.

You'll need to clone the EssentialsX repository
(`gh repo clone EssentialsX/Essentials`), then create a new branch for your work
(`cd Essentials` then `git switch -c my-cool-pr`).

You can then make changes using your IDE of choice. Follow the instructions in `README.md` to build and test your
changes.

Once you've finished, you should commit your changes (`git commit -am "My cool commit name!"`).

### Submitting your PR

*Even if you're already familiar with GitHub, you will need to follow these steps in order to submit a PR to
EssentialsX. This ensures we have the information we need to test and review your changes.*

Next, you'll need to fork EssentialsX on GitHub and push your changes to a branch on that fork. We strongly recommend
using the `gh` command-line tool for this:

* In your terminal, run `gh pr create`.
* Select either the bug fix or feature template, depending on what your PR is for.
* If you get asked `Where should we push the '...' branch?`, select `Create a fork of EssentialsX/Essentials`.
* For the title, choose a title that describes what your PR does. For example, `Add more ducks to /spawnmob`.
* For the body, press E. You will then need to fill in the PR template in your text editor.
  - Follow the instructions in the template and fill out the sections as required.
  - This step is important! Without it, we won't know why you've made your changes or how to test them.
* Save the file in your text editor and close it, then return to the terminal.
* Select `Submit` to open your PR.

If you follow these steps correctly, GitHub will create a fork (if necessary) and then open a PR with your changes. You
can now sit back while we review your changes. If we need to ask questions or request further changes to your code,
we will review your PR and post a comment on GitHub. You can respond to our reviews and comments on the GitHub website,
and you can push new changes using `git commit` and `git push`.


## Submitting community translations to EssentialsX

EssentialsX relies on community translations for its messages in other languages. You can help translate EssentialsX on
[Crowdin](https://translate.essentialsx.net/). You'll need a Crowdin account to translate and improve messages.

If your language isn't listed or doesn't have an active proofreader, please let us know on Discord (see below).


## Discussing EssentialsX contributions 

Want to discuss something before opening a PR or translating messages? Join the
[EssentialsX Development Discord server](https://discord.gg/CUN7qVb). Note that this server is **not for end-users** -
if you need support with EssentialsX, you should join [MOSS](https://discord.gg/casfFyh) instead.
