# bonita-userfilters

This project provides the Bonita Actor Filters feature.

An actor filter refines the list of users who can perform a task, by filtering the list of users mapped to the actor.


For more information see the Bonita documentation about
- [Actors](https://documentation.bonitasoft.com/bonita/7.9/actors)
- [Actor Filters](https://documentation.bonitasoft.com/bonita/7.9/actor-filtering)


## Contribution

I you want to contribute, ask questions about the project, report bug, see the [contributing guide][contributing_guide].


## Building the Project

### Prerequisites
>     Java JDK 1.8 (to compile), and JVM 8 or 11 (to run)

This project bundles the [Maven Wrapper][maven_wrapper], so the `mvnw` script is available at
the project root.


### Dependencies

The project depends on bonita-engine artifacts so if you want to build a branch in a SNAPSHOT version, you must build the
[bonita-engine][github_bonita_engine] first (install artifacts in your local repository).

If you build a tag, you don't need to build the bonita-engine as its artifacts are available on Maven Central.


### Building

Just run the following command:
```
./mvnw install -DskipTests
```

If you want to run tests, you must install all bonita-engine artifacts in your local Maven repository prior being able to build
`bonita-userfilters`.





[maven_wrapper]: https://github.com/takari/maven-wrapper
[github_bonita_engine]: https://github.com/bonitasoft/bonita-engine
[contributing_guide]: https://github.com/bonitasoft/bonita-developer-resources/blob/master/CONTRIBUTING.MD
