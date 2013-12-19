# JaVers - domain objects VCS#

## Abstract
JaVers is a lightweight java library for **versioning** your object-oriented data.

We all use Version Control Systems for source code,
why not to use specialized VCS for our applications to provide *versioning* of mission-critical data?

### Basic facts about JaVers
* It's lightweight and versatile. We don't take any assumptions about your data model, bean container or
  underlying data storage.
* Configuration is easy. Since we use JSON for objects serialization, we don't want you to
  provide detailed ORM-like mapping.
  JaVers needs to know only some high-level facts about your data model.
* We use some basic notions following Eric Evans DDD terminology like *Entity* or *Value Objects*,
  pretty much the same like JPA does. We believe that this is right way of describing data.


## Story

When developing an application, we usually concentrate on the current state of domain objects.
So we simply instantiate them, apply some changes and eventually, delete them, not paying much attention to previous states.

The challenge arises when a new requirement is discovered:
*As a User, I want to know who changed this status, when the change was performed and what was the previous status.*

The problem is, that both *version* and *change* notions are not easily expressible neither in the
Java language nor in the mainstream databases (although NoSQL document databases have advantage here over relational ones).

This is the niche JaVers fulfills. In JaVers, *version* and *change* are **first class citizens**.

## Core
The core functionality is calculating a diff between two graphs of objects.

## License
JaVers is licensed under Apache License Version 2.0, see LICENSE file.

## Project team
* Bartosz Walacik - owner,
* Pawel Cierpiatka - first contributor,
* Paweł Szymczyk - contributor.

### Former contributors
* Piotr Betkier
* Maciej Zasada

## CI status
[![Build Status](https://drone.io/bitbucket.org/javers/javers/status.png)](https://drone.io/bitbucket.org/javers/javers/latest)
