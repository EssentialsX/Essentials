Essentials Development Readme
=============================

The official repository is at:
https://github.com/essentials/Essentials

We use NetBeans 7.3 for development.

Recommended NetBeans plugins:

* Git
* PMD & FindBugs ( http://kenai.com/projects/sqe/pages/Home )

Building
--------
To build with Maven, use the command
```
mvn package dependency:copy
```

Jar files can then be found in the /jars folder


Commit Guidelines
-----------------

Commits should fall into one of 3 areas:

- `[Feature]`: Commits which are features should start with `[Feature]` and followed by a quick summary on the top line, followed by some extra details in the commit body.

- `[Fix]`: Commits which fix bugs, or minor improvements to existing features should start with `[Fix]` and followed by a quick summary on the top line, followed by some extra details in the commit body.

- Commits which fix bugs caused by previous commits (since last release), or otherwise make no functionality changes, should have no prefix.  These will not be added to the project change log.

Coding Guidelines
-----------------


Please follow the format guidelines that are saved in the project properties.

Windows users, please read this: http://help.github.com/line-endings/
The default line ending is **LF**.

To build all jars, select the EssentialsParent project and build that. You'll find all jars inside the jars folder.

Please only submit pull requests for the 2.x branch.

Bugs and issues can be submitted/found at https://essentials3.atlassian.net/


Other advice
-----------------

Not all features are 'Essentials Ready'.  Essentials is designed to cover the basic needs of Minecraft server administration, thus, we reject over 80% of feature requests that we deem are unsuitable.

Before developing an Essentials feature, we would recommend speaking to a developer in the Essentials IRC channel ([#essentials on irc.esper.net](http://tiny.cc/EssentialsChat)). Click [here](irc://irc.esper.net/#essentials) if you have a IRC client.
