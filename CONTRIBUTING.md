# How to contribute

## Guidelines for Bug Reporting
You can report a Bug or a Feature Request to our [github issues](http://github.com/javers/javers/issues/).
Questions should be asked at [stackoverflow.com](http://stackoverflow.com/questions/tagged/javers?sort=newest).
We'll answer them.

Bug Reports have to contain:

1. Clear description of your **expectations versus reality**
1. **Runnable test case** which isolates the bug and allows us to easily reproduce it on our laptops.
   You can push this test case to your fork of this repository. 

## Guidelines for Contributors

JaVers is an open source project, so we're open to your contributions.

In fact, if you need a new Feature,
the best way is to contribute a Pull Request. Otherwise you just have to wait  ...

Before you start to work please read these guidelines:

1. Create the **issue** with a full description of the new Feature
1. **Consult** the design with the JaVers team.
   You can chat with us on [gitter](https://gitter.im/javers/javers)
1. Source code should be written in **Java8**.
1. We aren't crazy about **code formatting** standards.
   Use 4 spaces to indent. Don't change the formatting of existing code.
1. We really care about **Clean Code** and **quality** so expect many Code Review comments.
1. **Tests** should be written in Spock/Groovy.<br/>
   In JaVers, tests are well-crafted, runnable documentation.<br/>
   Tests should specify and describe functionality, not implementation. <br/>
   We **fight Mocks**. **Stubs** are allowed but discouraged.
1. **Commit message** should [mention](https://github.com/blog/957-introducing-issue-mentions) the issue,
   for example:<br/>
   `#299 new Guidelines for Contributors in README.md`
1. Before creating a **Pull Request**, merge from master and
   squash your commits
1. Apply our **Code Review** comments and commit changes in the next commit.
Please don't squash Code Review commits, we want to track the progress of the Code Review process.  