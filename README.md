Reproducer for the issue that just the presence of a `org.gradle.api.artifacts.DependencySubstitutions.all()`
causes dependencies to be resolved with the `default` variant instead of e.g. `compile` or `runtime` variants.

There are 3 modules in this project:
* `prj1` + `prj2` - two dummies with a single class in each
* `aggregator` collects the runtime dependencies from the other two projects to get a machine dependendent
  classpath over both modules.

Note:
"Dependency management" must be applied to all configurations for prj1+prj2 and the "runtime" configuration
of the aggregator project. Otherwise, in a much bigger project with way more dependencies, transient dependencies
could sneak in with the wrong version - so dep-handling is applied to :aggregator:runtime
