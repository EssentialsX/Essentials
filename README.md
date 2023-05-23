[![EssentialsX](https://i.imgur.com/CP4SZpB.png)](https://essentialsx.net)

[<img alt="Dev Builds" src="https://img.shields.io/badge/-Download_dev_builds-D24939.svg?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAMAAACdt4HsAAAAdVBMVEVHcEz////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////qLaloAAAAJnRSTlMA/go0+C4DzJ2IHr/rlvBznq9cGClUqdEiQuR74RHcPbdqw15Ykw3WSecAAAENSURBVFjD7ZdJFoIwEERpEgUBRXCe577/EV2ahBi6dKUvf1//BbpoIEn+lsvI4QIKFuywQI9Q2vkrfA0DMvM0wO/C1RSUH9zGqSmYfiDITUEeBVEQBVHw04Jvd+J8Ygom8Fqf7e0Xy7bA8unKfbWtDkh+eOYO5RAQjNkDMIkde9lJ83fyC+gmy580v0Fv8ALYSOrgFgCtQ5pxkCztKUDDPZzDdXj05ZmCdahZQB34tiOJgFq8ALI6rCsWUs19+cORxRxnngGWDOB5tmuG6Iyi0FCedCFZISHGjqBCBZUzQoaxr0HhAmX/HeACe7ekGoyTdhZDgx6gcaagwCPoznZsIYP2PNJqmYlZqiTy4gka59N5SBiJlAAAAABJRU5ErkJggg==&style=flat-square&logoColor=white" height=32>](https://essentialsx.net/downloads.html)

[<img alt="Discord" src="https://img.shields.io/badge/-Get_help_on_Discord-7289DA.svg?logo=discord&style=flat-square&logoColor=white" height=32>](https://discord.gg/casfFyh)

[<img alt="Patreon" src="https://img.shields.io/badge/-Support_on_Patreon-F96854.svg?logo=patreon&style=flat-square&logoColor=white" height=32>](https://www.patreon.com/essentialsx)

This is a fork of Essentials called EssentialsX.

If you are using this, do **NOT** ask Essentials for support.

The official upstream repository for the original Essentials project is at https://github.com/Essentials/Essentials.


## Why use EssentialsX?

EssentialsX is a continuation of the Essentials plugin suite, updated to support modern Minecraft and Spigot versions.

It provides countless new features, performance enhancements and fixes that are not available in the original
Essentials or Spigot-Essentials. [For more details, see the wiki.](https://essentialsx.net/wiki/Improvements.html)

If you're coming from the original Essentials plugin, EssentialsX is a drop-in replacement for Essentials. It does,
however, have some new requirements:

* **EssentialsX requires CraftBukkit, Spigot or Paper to run.** Other server software may work, but these are not tested
  by the team and we may not be able to help with any issues that occur.
* **EssentialsX currently supports Minecraft versions 1.8.8, 1.9.4, 1.10.2, 1.11.2, 1.12.2, 1.13.2, 1.14.4, 1.15.2, 
  1.16.5, 1.17.1, 1.18.2, and 1.19.4.**
* **EssentialsX currently requires Java 8 or higher.** We recommend using the latest Java version supported by your
  server software.
* **EssentialsX requires [Vault](http://dev.bukkit.org/bukkit-plugins/vault/) to enable using chat prefix/suffixes and
  group support from supported permissions plugins.**
  - **We recommend using [LuckPerms](https://luckperms.net) for permissions and groups.**
  - If you have an unsupported permissions plugin but still want to use wildcards, **enable `use-bukkit-permissions` in
    the configuration.** Otherwise, the plugin will fall back to config-based permissions.


## Support

Need help with using EssentialsX? Join the [MOSS Discord community](https://discord.gg/casfFyh) to ask for help and discuss EssentialsX.

If you need to report a bug or want to suggest a new feature, you can [open an issue on GitHub](https://github.com/EssentialsX/Essentials/issues/new/choose).


## Building

To build EssentialsX, you need JDK 8 or higher installed on your system.

Clone this repository, then run the following command:

* On Linux or macOS: `./gradlew build`
* On Windows: `gradlew build`

You can then find builds of EssentialsX modules in the `jars/` directory.

### Running a test server

You can also run a test server from your development environment using the following command:

* On Linux or macOS: `./gradlew build :runServer`
* On Windows: `gradlew build :runServer`

Note the `:` - without it, you will run several servers at once, which will likely crash Gradle.


## Using EssentialsX in your plugin

Do you want to integrate with EssentialsX in your plugin? You can build your plugin against the **EssentialsX API**,
available from the EssentialsX Maven repo.

Releases are hosted on the Maven repo at `https://repo.essentialsx.net/releases/`, while snapshots (including dev
builds) are hosted at `https://repo.essentialsx.net/snapshots/`.

To add EssentialsX to your build system, you should use the following artifacts:

| Type           | Group ID          | Artifact ID   | Version           |
|:---------------|:------------------|:--------------|:------------------|
| Latest release | `net.essentialsx` | `EssentialsX` | `2.20.0`          |
| Snapshots      | `net.essentialsx` | `EssentialsX` | `2.20.1-SNAPSHOT` |
| Older releases | `net.ess3`        | `EssentialsX` | `2.18.2`          |

Note: until version `2.18.2`, EssentialsX used the `net.ess3` group ID.  
From `2.19.0` onwards, EssentialsX uses the `net.essentialsx` group ID.  
When updating your plugin, make sure you use the correct group ID.

You can find more information, including Maven and Gradle examples, at the
[wiki](https://essentialsx.net/wiki/Common-Issues.html#how-do-i-add-essentialsx-as-a-dependency).

## Support the EssentialsX project

Want to help improve EssentialsX? There are several ways you can support and contribute to the project.

### Donate to EssentialsX

Donations allow us to cover the costs of our infrastructure, and also enable us to keep updating EssentialsX with new
features and for new Minecraft versions.

You can support us with a one-off or monthly donation via [GitHub Sponsors](https://github.com/sponsors/EssentialsX),
and you'll get a badge on GitHub for supporting the project through this.

Alternatively, you can also donate monthly to the EssentialsX project on [Patreon](https://www.patreon.com/essentialsx/),
or you can make a one-off donation on our [Ko-fi page](https://ko-fi.com/essentialsx).

If you can't make a donation, don't worry! There are lots of other ways to contribute:

### Contributing directly to EssentialsX

* Are you a developer? We're always happy to receive bug fixes and feature additions as pull requests.
* Do you speak multiple languages? If so, we always welcome contributions to our community translations.
  [Crowdin project](https://crowdin.com/project/essentialsx-official).

See [CONTRIBUTING.md](https://github.com/EssentialsX/Essentials/blob/2.x/CONTRIBUTING.md) to find out more.

### Providing support to other users

* Do you run a server? Take a look at our
  ["help wanted"](https://github.com/EssentialsX/Essentials/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc+label%3A%22help+wanted%22)
  and ["bug: unconfirmed"](https://github.com/EssentialsX/Essentials/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc+label%3A%22bug%3A+unconfirmed%22)
  issues, where you can find issues that need extra testing and investigation.
* Do you want to help others set up EssentialsX? You can contribute to the
  [EssentialsX docs](https://github.com/EssentialsX/wiki). You can also join the
  [MOSS Discord community](https://discord.gg/casfFyh) and provide direct community support to other EssentialsX users.
