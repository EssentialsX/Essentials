[![EssentialsX](https://i.imgur.com/CP4SZpB.png)](https://essentialsx.net)

[<img alt="Dev Builds" src="https://img.shields.io/badge/-Download_dev_builds-D24939.svg?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAMAAACdt4HsAAAAdVBMVEVHcEz////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////qLaloAAAAJnRSTlMA/go0+C4DzJ2IHr/rlvBznq9cGClUqdEiQuR74RHcPbdqw15Ykw3WSecAAAENSURBVFjD7ZdJFoIwEERpEgUBRXCe577/EV2ahBi6dKUvf1//BbpoIEn+lsvI4QIKFuywQI9Q2vkrfA0DMvM0wO/C1RSUH9zGqSmYfiDITUEeBVEQBVHw04Jvd+J8Ygom8Fqf7e0Xy7bA8unKfbWtDkh+eOYO5RAQjNkDMIkde9lJ83fyC+gmy580v0Fv8ALYSOrgFgCtQ5pxkCztKUDDPZzDdXj05ZmCdahZQB34tiOJgFq8ALI6rCsWUs19+cORxRxnngGWDOB5tmuG6Iyi0FCedCFZISHGjqBCBZUzQoaxr0HhAmX/HeACe7ekGoyTdhZDgx6gcaagwCPoznZsIYP2PNJqmYlZqiTy4gka59N5SBiJlAAAAABJRU5ErkJggg==&style=flat-square&logoColor=white" height=32>](https://essentialsx.net/downloads.html)

[<img alt="Discord" src="https://img.shields.io/badge/-Chat_on_Discord-7289DA.svg?logo=discord&style=flat-square&logoColor=white" height=32>](https://discord.gg/casfFyh)

[<img alt="Patreon" src="https://img.shields.io/badge/-Support_on_Patreon-F96854.svg?logo=patreon&style=flat-square&logoColor=white" height=32>](https://www.patreon.com/essentialsx)

This is a fork of Essentials called EssentialsX.

If you are using this, do **NOT** ask Essentials for support.

The official upstream repository for the original Essentials project is at https://github.com/Essentials/Essentials.


Why use EssentialsX?
--------------------

EssentialsX is an unofficial continuation of Essentials, updated to support modern Minecraft and Spigot versions. It provides several performance enhancements and fixes that are currently not available in Essentials and Spigot-Essentials. [For more details, see the wiki.](https://essentialsx.net/wiki/Improvements.html)

EssentialsX is almost a completely drop-in replacement for Essentials. However, it has different requirements:

* **EssentialsX requires [Vault](http://dev.bukkit.org/bukkit-plugins/vault/) to enable chat prefix/suffixes and group support if you have a supported permissions plugin.** We recommend using [LuckPerms](https://luckperms.github.io).

* **If you have an unsupported permissions plugin but still wish to use wildcards, enable `use-bukkit-permissions` in the configuration.** Otherwise, the plugin will fall back to config-based permissions.

* **EssentialsX requires Java 8 or higher.** On older versions, the plugin may not work properly.

* **EssentialsX supports Minecraft versions 1.8.8, 1.9.4, 1.10.2, 1.11.2, 1.12.2, 1.13.2, 1.14.4, 1.15.2, 1.16.5, and 1.17.1**


Support
-------

Need help with using EssentialsX? Join the [MOSS Discord community](https://discord.gg/casfFyh) to ask for help and discuss EssentialsX.

If you need to report a bug or want to suggest a new feature, you can [open an issue on GitHub](https://github.com/EssentialsX/Essentials/issues/new/choose).


Building
--------

To build EssentialsX, you need JDK 8 or higher installed on your system. Then, run the following command:
```sh
./gradlew build
```

...or if you're on windows run the following command:

```batch
gradlew build
```

Each module's jar can be found in `build/libs/` inside each module's directory or in `jars/`.


Using EssentialsX in your plugin
--------------------------------

Do you want to integrate with EssentialsX in your plugin? You can use the EssentialsX Maven repo to build against EssentialsX's API.

Releases are hosted on the Maven repo at `https://repo.essentialsx.net/releases/`, while snapshots (including dev builds) are hosted at `https://repo.essentialsx.net/snapshots/`.

To add EssentialsX to your build system, you should use the following artifacts:

| Type            | Group ID        | Artifact ID | Version         |
| :-------------- | :-------------- | :---------- | :-------------- |
| Latest release  | net.essentialsx | EssentialsX | 2.19.0
| Snapshots       | net.essentialsx | EssentialsX | 2.19.1-SNAPSHOT |
| Older releases  | net.ess3        | EssentialsX | 2.18.2          |

Note: up until `2.18.2`, EssentialsX used the `net.ess3` group ID, but starting with `2.19.0` snapshots, the group ID is now `net.essentialsx`.
When updating your plugin, make sure you use the correct group ID.

You can find more information and examples at the [wiki](https://essentialsx.net/wiki/Common-Issues.html#how-do-i-add-essentialsx-as-a-dependency).

Contributing
------------

Want to help improve EssentialsX? There are several ways you can support and contribute to the project.

If you'd like to make a financial contribution to the project, you can join our [Patreon](https://www.patreon.com/essentialsx/),
or to make a one-off donation you can visit our [Ko-fi page](https://ko-fi.com/essentialsx). If you can't make a
donation, don't worry! There are lots of other ways to contribute:

* Do you run a server? Take a look at our ["help wanted"](https://github.com/EssentialsX/Essentials/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc+label%3A%22help+wanted%22)
  and ["bug: unconfirmed"](https://github.com/EssentialsX/Essentials/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc+label%3A%22bug%3A+unconfirmed%22)
  issues, where you can find issues that need extra testing and investigation.
* Do you speak multiple languages? If so, we always welcome contributions to our [Crowdin project](https://crowdin.com/project/essentialsx-official).
* Do you enjoy helping others? If so, why not contribute to the [EssentialsX documentation](https://github.com/EssentialsX/wiki)?
  You can also join the [MOSS Discord community](https://discord.gg/casfFyh) and provide direct community support to
  other EssentialsX users.
* If you're a developer, you could look through our ["open to PR"](https://github.com/EssentialsX/Essentials/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc+label%3A%22status%3A+open+to+PR%22)
  issues. We're always happy to receive bug fixes and feature additions as pull requests.

See [CONTRIBUTING.md](https://github.com/EssentialsX/Essentials/blob/2.x/CONTRIBUTING.md) to find out more.
