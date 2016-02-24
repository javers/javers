﻿
![javers-black-logo-1.0.png](javers-black-logo-1.0.png)

## What is JaVers

[![Join the chat at https://gitter.im/javers/javers](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/javers/javers?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Ideal for contribution](https://badge.waffle.io/javers/javers.svg?label=ideal%20for%20contribution&title=Ideal for contribution)](http://waffle.io/javers/javers)

JaVers is the lightweight Java library for **auditing** changes in your data.

We all use Version Control Systems for source code,
so why not use a specialized framework to provide
an audit trail of your Java objects (entities, POJO, data objects)?

## Check out our site
You can find the latest information about JaVers project at [javers.org](http://javers.org).
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

Run the environment self-test, to check if JaVers is compatible with your JDK version

```
./gradlew javers-core:run
```

## Issues and Contributing
We'd love to get issues and contributions from you!

You can report a Bug or a Feature Request.
Questions should be asked at [stackoverflow.com](http://stackoverflow.com/questions/tagged/javers?sort=newest).
We'll answer them.
Before you create an issue please read our
[Guidelines for Bug Reporting](CONTRIBUTING.md#guidelines-for-bug-reporting).

JaVers is an open source project, so we are open to your contributions.
Before you start, please read our
[Contributing Guide](CONTRIBUTING.md#guidelines-for-contributors) and see how to get your changes merged.

## Team Board
Check out our [Kanban Board](https://waffle.io/javers/javers) to see what we're working on. 

## CI status
[![Build Status](https://travis-ci.org/javers/javers.png?branch=master)](https://travis-ci.org/javers/javers)

## License
JaVers is licensed under Apache License Version 2.0, see the LICENSE file.