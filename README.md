﻿
![javers-black-logo-1.0.png](javers-black-logo-1.0.png)

## What is JaVers

[![Join the chat at https://gitter.im/javers/javers](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/javers/javers?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
JaVers is a lightweight java library for **auditing** changes in your data.

We all use Version Control Systems for source code,
why not to use specialized framework to provide an audit trail of your Java objects (entities, POJO, data objects)?

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
gradlew build
```

Run environment self-test, to check if JaVers is compatible with your JDK version

```
gradlew javers-core:run
```



## Project Team
Check out our site to find [the team](http://javers.org/#team) and contact us.

## Report an issue
You found a bug? Documentation is unclear?

Report an issue to our [github issues](http://github.com/javers/javers/issues/).

## CI status
[![Build Status](https://travis-ci.org/javers/javers.png?branch=master)](https://travis-ci.org/javers/javers)

## License
JaVers is licensed under Apache License Version 2.0, see LICENSE file.