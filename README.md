This is a reproducer project for https://github.com/gradle/gradle/issues/12951.

To demonstrate the issue:
- Run `./gradlew :prj-lib:dependencyInsight --configuration compileClasspath --dependency jffi`. You will see the "compile" variant selected
- Run `./gradlew :prj-lib:dependencyInsight --configuration compileClasspath --dependency jffi aggregate`. You will see the "default" variant selected, due simply to the presence of the `aggregate` task.

This issue can also be demonstrated without the dependency substitution rule, by adding `-Dorg.gradle.resolution.assumeFluidDependencies=true` to the CLI.
