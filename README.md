[![Build Status](https://travis-ci.com/eXist-db/expath-pkg-java.png?branch=develop)](https://travis-ci.com/eXist-db/expath-pkg-java)

# EXPath Packaging for Java

**NOTE:** This is a fork and advancement of the code taken from [fgeorges/expath-pkg-java](https://github.com/fgeorges/expath-pkg-java). Unfortunately we were unable to get changes via PR upstreamed, so we have forked the project here.

Implementation of the EXPath [Packaging System](http://expath.org/modules/pkg/)
for Java, Saxon and Calabash.


## Installation

Download the latest `expath-repo-installer-x.y.z.jar` file from the
[download area](http://expath.org/files) and execute it by
double-clicking on it (or from the command-line: `java -jar
expath-repo-installer-x.y.z.jar`).  Follow the instructions, that's
it!

If you don't want a graphical installer, use the latest
`expath-repo-x.y.z.zip` file instead.  Unzip it and set it up.


## Setup

Add the script `xrepo` to your PATH.  To create an EXPath package
repository, simply use: `xrepo create <repo-dir>`.  You can set the
environment variable `EXPATH_REPO` if you don't want to provide the
repository directory everytime you use `xrepo`.

For more information on the repository manager: `xrepo help`.

The install directory contains also a script `saxon` and a script
`calabash`, that launch resp. Saxon and Calabash, by configuring them
with the repository, and adding useful options from the command line.
For more information: `saxon --help` and `calabash ++help`, or see the
[README](https://github.com/fgeorges/expath-pkg-java/tree/master/bin)
file in the sub-directory `bin/` in the source repository.
