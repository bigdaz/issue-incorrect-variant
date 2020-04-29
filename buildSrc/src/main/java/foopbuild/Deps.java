/*
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */

package foopbuild;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySubstitution;
import org.gradle.api.artifacts.DependencySubstitutions;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "Convert2Lambda", "NullableProblems" })
public class Deps
{
    public static Deps withDependencyHandling(Project p)
    {
        Deps deps = Deps.deps(p);

        deps.dependencyHandlingForProject(p);

        return deps;
    }

    public static Deps deps(Project p)
    {
        Project rootProject = p.getRootProject();
        Deps serverDeps = (Deps) rootProject.getExtensions().findByName("serverDeps");
        if (serverDeps == null)
        {
            serverDeps = new Deps();
            rootProject.getExtensions().add("serverDeps", serverDeps);
        }
        return serverDeps;
    }


    private void substituteDependenciesWithPatternMatching(Configuration c)
    {
        c.getResolutionStrategy().dependencySubstitution(new Action<>()
        {
            @Override
            public void execute(DependencySubstitutions ds)
            {
                // ****************************************************
                //
                // Just the presence of `ds.all()` with an empty exception basically trashes Gradle's dependency resolution
                // (looks like a legit bug in Gradle). Dependencies that should have been resolved with e.g. the 'runtime'
                // variant (think: configuration), get resolved using the 'default' variant, which is wrong.
                // Doing the same with individual `ds.substitute()`s, works, but is too verbose and too strict to be maintainable.
                //
                // ****************************************************
                ds.all(new Action<>()
                {
                    @Override
                    public void execute(DependencySubstitution dependency)
                    {
                        //
                    }
                });
                // ****************************************************
                //
                // ^ ^ ^ ^ That's the ds.all() that lets the dependencies be resolved with the 'default' variant
                //
                // ****************************************************
            }
        });
    }

    @SuppressWarnings("Anonymous2MethodRef")
    private final Action<Configuration> configurationConfigureAction = new Action<>()
    {
        @Override
        public void execute(Configuration c)
        {
            substituteDependenciesWithPatternMatching(c);
        }
    };

    /**
     * Apply dependency handling to a all configurations of a project.
     */
    public void dependencyHandlingForProject(Project p)
    {
        p.getConfigurations().configureEach(configurationConfigureAction);
    }
}
