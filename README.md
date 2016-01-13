﻿
![javers-black-logo-1.0.png](javers-black-logo-1.0.png)

## What is JaVers

[![Join the chat at https://gitter.im/javers/javers](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/javers/javers?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Ideal for contribution](https://badge.waffle.io/javers/javers.svg?label=ideal%20for%20contribution&title=Ideal for contribution)](http://waffle.io/javers/javers)

JaVers is the lightweight Java library for **auditing** changes in your data.

We all use Version Control Systems for source code,
why not to use specialized framework to provide the audit trail of your Java objects (entities, POJO, data objects)?

## Check out our site
You can find latest information about JaVers project at [javers.org](http://javers.org).
Check out our [documentation](http://javers.org/documentation)</a> pages.

## Build & test JaVers
Clone our github repository

```
git clone https://github.com/javers/javers.git
cd javers
```

Build JaVers and run unit tests

```
./gradlew build
```

Run environment self-test, to check if JaVers is compatible with your JDK version

```
./gradlew javers-core:run
```

## Guidelines for Bug Reporting
You can report a Bug or a Feature request to our [github issues](http://github.com/javers/javers/issues/).
Questions should be asked at [stackoverflow.com](http://stackoverflow.com), we will answer.

Bug Reports have to contain:

1. Clear description of your **expectations versus reality**
1. **Runnable test case** which isolates the bug and allows us to easily reproduce it on our laptops.
   You can push this test case to your fork of this repository. 

## Guidelines for Contributors

JaVers is an open source project, we are open to your contributions.

In fact, if you need a new Feature,
the best way is to contribute a Pull Request. Otherwise you can only wait ...

Before you start to work, please read this guidelines:

1. Create the **issue** with full description of a new Feature
1. **Consult** the design with JaVers team.
   You can chat with us on [gitter](https://gitter.im/javers/javers)
1. Source code should be written in **Java7**.
   Please don't import Java8 types like `java.util.Optional`.
   The only exception is `package org.javers.java8support`
1. We are not crazy about **code formatting** standards.
   Use 4 spaces to indent. Don't change formatting of existing code.
1. We really care about **Clean Code**, **quality** so expect many Code Review comments.
1. **Tests** should be written in Spock/Groovy. In tests, Java8 is fine.<br/>
   In JaVers, tests are well-crafted, runnable documentation.<br/>
   Tests should specify and describe functionality, not implementation. <br/>
   We **fight Mocks**, **Stubs** are allowed but discouraged.
1. **Commit message** should [mention](https://github.com/blog/957-introducing-issue-mentions) the issue,
   for example:<br/>
   `#299 new Guidelines for Contributors in README.md`
1. Squash your commits into one and create the **Pull Request**
1. Apply our **Code Review** comments and commit changes in the next commit.
Please don't squash Code Review commits, we want to track progress of the Code Review process.    


## Team Board
See on our [Kanban Board](https://waffle.io/javers/javers) what are working on. 

## CI status
[![Build Status](https://travis-ci.org/javers/javers.png?branch=master)](https://travis-ci.org/javers/javers)

## License
JaVers is licensed under Apache License Version 2.0, see LICENSE file.