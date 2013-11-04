# JaVers - domain objects VCS#

## Abstract
JaVers is a tool for tracking changes in object-oriented data.

Most modern applications have a multi-layered architecture, as follows:

1. UI layer,
1. domain model layer,
1. persistence layer.

Domain model layer is where your Entity instances (aka domain objects) live. Persistence layer takes responsibility for saving and loading them from the database.

When developing an application, we usually concentrate on the current state of domain objects. So we simply instantiate them, apply some changes and eventually, delete them, not paying much attention to previous states.

The challenge arises when a new requirement is discovered: *As a User, I want to know who changed this status, when the change was performed and what was the previous status.*

The problem is, that both *version* and *change* notions are not easily expressible neither in the Java language nor in the mainstream databases (although NoSQL document databases have advantage here over relational ones).

This is the niche JaVers fulfills. In JaVers, *version* and *change* are **first class citizens**.

## Core
The core functionality is calculating a diff between two graphs of objects.

## License
JaVers is licensed under Apache License Version 2.0, see LICENSE file.

## Project team
* Bartosz Walacik - owner,
* Pawel Cierpiatka - first contributor,
* Maciej Zasada  - contributor,
* Piotr Betkier - contributor,
* Paweł Szymczyk - contributor.

## CI status
[![Build Status](https://drone.io/bitbucket.org/javers/javers/status.png)](https://drone.io/bitbucket.org/javers/javers/latest)
