
![JVlogo.png](JVlogo2.png)

## Abstract
JaVers is a lightweight java library for **auditing** your object-oriented data.

We all use Version Control Systems for source code,
why not to use specialized framework to provide for our application an audit trail of domain objects?

## Story

When developing an application, we usually concentrate on the current state of domain objects.
So we simply instantiate them, apply some changes and eventually, delete them, not paying much attention to previous states.

The challenge arises when a new requirement is discovered:
*As a User, I want to know who changed this status, when the change was performed and what was the previous status.*

The problem is, that both *version* and *change* notions are not easily expressible neither in the
Java language nor in the mainstream databases (although NoSQL document databases have advantage here over relational ones).

This is the niche JaVers fulfills. In JaVers, *version* and *change* are **first class citizens**.

## Vision
  With JaVers 1.0 you would be able to perform following operations:

* Commit changes performed on your objects graph with single commit() call.
* Browse detailed diffs, scoped on object graph level,
  to easily track changes of object field values as well as changes of relations between objects.
* Browse *shadows* - historical versions of object graph loaded directly into your data model classes.

## Basic facts about JaVers
* It's lightweight and versatile. We don't take any assumptions about your data model, bean container or
  underlying data storage.
* Configuration is easy. Since we use JSON for objects serialization, we don't want you to
  provide detailed ORM-like mapping.
  JaVers needs to know only some high-level facts about your data model.
* JaVers is meant to keep its versioning records (diffs and snapshots) in
  application primary database alongside with main data.
  Obviously there is no direct linking between these two data sets.
* We use some basic notions following Eric Evans DDD terminology like *Entity* or *Value Objects*,
  pretty much the same like JPA does. We believe that this is right way of describing data.

## Core
* The core functionality is calculating a diff between two graphs of objects.
* TBA

## License
JaVers is licensed under Apache License Version 2.0, see LICENSE file.

## Project team
* Bartosz Walacik - owner, commiter
* Paweł Szymczyk - committer
* Michał Szostakowski - committer

### Former commiters
* Pawel Cierpiatka - committer,
* Piotr Betkier
* Maciej Zasada

## CI status
[![Build Status](https://travis-ci.org/javers/javers.png?branch=master)](https://travis-ci.org/javers/javers)
