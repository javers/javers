#JaVers - domain objects VCS#

##License##
JaVers is licensed under Apache License Version 2.0, see LICENSE file.

##Project team##
* Bartosz Walacik - owner
* Pawel Cierpiatka - first contributor

## Abstract
JaVers is a tool for tracking changes in object-oriented data.

Most modern applications has multi-layered architecture, as follows:

1. UI layer
1. domain model layer
1. persistence layer

In domain model layer lives your Entity instances (aka domain objects),
persistence layer takes responsibility for saving and loading them from database.

When application is being developed, we usually concentrate on current state of domain objects.
So we simply instantiates them, apply some changes and eventually, delete them,
not paying much attention about previous states.

The challenge arises when new requirement is discovered:
As a User, I want to know who changed this status, when the change was performed and what was the previous status.

The problem is, that both *version* and *change* notions are not easily expressible
nor in Java language nor in mainstream databases (although NoSQL document databases has advantage here over relational ones).

That is the niche JaVers fulfills. In JaVers, *version* and *change* are **first class citizens**.

## Core
The core functionality is calculating diff between two graphs of objects.