This is a reproducer project for https://github.com/gradle/gradle/issues/12951.

To demonstrate the issue:
- Run `./gradlew :prj-lib:dependencyInsight --configuration compileClasspath --dependency jffi`. You will see the "compile" variant selected
- Run `./gradlew :prj-lib:dependencyInsight --configuration compileClasspath --dependency jffi aggregate`. You will see the "default" variant selected, due simply to the presence of the `aggregate` task.

This issue can also be demonstrated without the [dependency substitution rule](https://github.com/bigdaz/issue-incorrect-variant/blob/master/prj-resolve/build.gradle#L5-L8), by adding `-Dorg.gradle.resolution.assumeFluidDependencies=true` to the CLI.

This bug can have a real impact on the `compileClasspath`: when the `aggregate` task is requested, the `compileClasspath` of `:prj-lib` includes `jffi-1.2.19-native.jar`, which is scoped for `runtime` usage. When the `aggregate` task isn't requested, or when there is no substitution rule, this jar is not included in the `compileClasspath`.
