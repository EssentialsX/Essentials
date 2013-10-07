Essentials Development Readme
=============================

The official repository is at:
https://github.com/essentials/Essentials

We use NetBeans 7.3 for development.

Recommended NetBeans plugins:

* Git
* PMD & FindBugs ( http://kenai.com/projects/sqe/pages/Home )

Commit Guidelines
-----------------

Commits should fall into one of 3 areas:

[Feature] Commits which are features should start with '[Feature]' and followed by a quick summary on the top line, followed by some extra details in the commit body.

[Fix] Commits which fix bugs, or minor improvements to existing features should start with '[Fix]' and followed by a quick summary on the top line, followed by some extra details in the commit body.

Commits which fix bugs caused by previous commits (since last release), or otherwise make no functionality changes, should have no prefix.  These will not be added to the project change log.

Coding Guidelines
-----------------


Please follow the format guidelines that are saved in the project properties.

Windows users, please read this: http://help.github.com/line-endings/
The default line ending is LF.

To build all jars, select the BuildAll project and build that. You'll find all jars inside the dist/lib folder of the BuildAll project.

If you create pull requests, always make them for the 2.x branch.

The essentials bug tracker can be found at https://essentials3.atlassian.net/


Other advice
-----------------

Not all features are 'Essentials Ready'.  Essentials is designed to be cover the basic needs of Minecraft server administration, for this reason, we reject over 80% of feature requests.

Before developing an Essentials feature, we would recommend speaking to a developer in the Essentials IRC channel (#essentials on irc.esper.net).
