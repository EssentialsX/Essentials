![](https://i.imgur.com/mwJjTRw.png)

[![Downloads](http://i.imgur.com/MNr6OTb.png)](https://ci.drtshock.net/job/EssentialsX)

This is a fork of Essentials called EssentialsX.

If you are using this, do **NOT** ask Essentials for support.

The official upstream repository is at https://github.com/Essentials/Essentials

Why you should use it
--------

EssentialsX provides several performance enhancements and fixes that are currently not available in Essentials and Spigot-Essentials, most importantly fixed mob spawner support for 1.8+ servers. [See the wiki for details.](https://github.com/drtshock/Essentials/wiki)

EssentialsX is almost a completely drop-in replacement for Essentials. However, it has different requirements:

* **EssentialsX requires [Vault](http://dev.bukkit.org/bukkit-plugins/vault/) to enable chat prefix/suffixes and group support if you have a supported permissions plugin.**

* **If you have an unsupported permissions plugin but still wish to use wildcards, enable `use-bukkit-permissions` in the configuration. Otherwise, the plugin will fall back to config-based permissions.**

* **EssentialsX requires Java 7.**

Building
--------

Because EssentialsX builds against the Spigot/CraftBukkit server software for legacy support, you need to run Spigot's BuildTools for several versions in order to get it to compile.

```
java -jar BuildTools.jar --rev 1.8
java -jar BuildTools.jar --rev 1.8.3
java -jar BuildTools.jar
```

Then, to build with Maven, use the command
```
mvn clean install
```

Jar files can then be found in the /target folder for each module.


Commit Guidelines
-----------------

Commits should fall into one of 3 areas:

- `[Feature]`: Commits which are features should start with `[Feature]` and followed by a quick summary on the top line, followed by some extra details in the commit body.

- `[Fix]`: Commits which fix bugs, or minor improvements to existing features should start with `[Fix]` and followed by a quick summary on the top line, followed by some extra details in the commit body.

- Commits which fix bugs caused by previous commits (since last release), or otherwise make no functionality changes, should have no prefix.  These will not be added to the project change log.


Other Info
-----------------

This is an unofficial fork of Essentials. It will be consistently updated and maintained with the latest Minecraft and Spigot versions.

Support
-----------------
[Issue Tracker](https://github.com/drtshock/Essentials/issues)

[Live Support](http://webchat.esper.net/?channels=drtshock&prompt=1) available business hours (GMT -5)
