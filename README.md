﻿
![logotype.svg](https://javers.org/img/logotype.svg)

[![Maven Central](https://img.shields.io/maven-central/v/org.javers/javers-core)](https://central.sonatype.com/artifact/org.javers/javers-core)
[![Build Status](https://travis-ci.org/javers/javers.svg?branch=master)](https://travis-ci.org/javers/javers/)
[![Financial Contributors on Open Collective](https://opencollective.com/javers/all/badge.svg?label=financial+contributors)](https://opencollective.com/javers)

> ### JaVers Pro — Enterprise Audit Infrastructure
>
> JaVers is evolving to **Open Core**. The open-source core you rely on remains **100% free, forever**.
>
> **JaVers Pro** adds the professional infrastructure enterprise teams need:
> - **Audit Explorer UI** — visual audit trail browser for non-technical stakeholders
> - **Enterprise Performance** — multitenancy, high-throughput data retention policies
> - **Compliance Tooling** — built-in support for GDPR, SOX, and HIPAA requirements
>
> **[Join the early-access waitlist](https://javers.org/waiting-list)** and lock in a **40% lifetime discount** as a design partner.

## What is JaVers

JaVers is the lightweight Java library for **auditing** changes in your data.

We all use Version Control Systems for source code,
so why not use a specialized framework to provide
an audit trail of your Java objects (entities, POJO, data objects)?

**Key features:**
- **Object diff** — compare complex object graphs with a single call
- **Audit log** — auto-track every change with commit metadata, author, and timestamps
- **Shadow queries** — reconstruct historical object state at any point in time
- **Spring Boot & Spring Data** integration — auto-audit your repositories with a single `@JaversSpringDataAuditable` annotation
- **Flexible persistence** — MongoDB, SQL (Hibernate/JPA), or Polymorphic support

See the full [feature list](https://javers.org/features) for more details.

## Quick Start

Add JaVers to your project:

**Gradle (Spring Boot starter with SQL)**
```groovy
implementation 'org.javers:javers-spring-boot-starter-sql:{{latest}}'
```

**Maven**
```xml
<dependency>
    <groupId>org.javers</groupId>
    <artifactId>javers-spring-boot-starter-sql</artifactId>
    <version>{{latest}}</version>
</dependency>
```

For MongoDB, use `javers-spring-boot-starter-mongo` instead.
Check the latest version on [Maven Central](https://central.sonatype.com/artifact/org.javers/javers-core).

See the full [Getting Started guide](https://javers.org/documentation/getting-started/) for setup instructions and examples.

## Documentation

Visit [javers.org](https://javers.org) for the latest information about the JaVers project.
Browse the full [documentation](https://javers.org/documentation) for guides, configuration, and API reference.

## Build & test JaVers
Clone our GitHub repository

```
git clone https://github.com/javers/javers.git
cd javers
```

Build JaVers and run unit tests

```
./gradlew build
```

## Issues and Contributing
We'd love to get issues and contributions from you!

You can report a Bug or a Feature Request.
Questions should be asked at [stackoverflow.com](https://stackoverflow.com/questions/tagged/javers?sort=newest).
We'll answer them.
Before you create an issue please read our
[Guidelines for Bug Reporting](CONTRIBUTING.md#guidelines-for-bug-reporting).

JaVers is an open source project, so we are open to your contributions.
Before you start, please read our
[Contributing Guide](CONTRIBUTING.md#guidelines-for-contributors) and see how to get your changes merged.

### Code Contributors

This project exists thanks to all the people who contribute their work:
<a href="https://github.com/javers/javers/graphs/contributors"><img src="https://opencollective.com/javers/contributors.svg?width=890&button=false" /></a>

## License
JaVers is licensed under Apache License Version 2.0, see the LICENSE file.
