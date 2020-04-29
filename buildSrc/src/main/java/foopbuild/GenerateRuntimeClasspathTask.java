/*
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */

package foopbuild;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

@CacheableTask
public class GenerateRuntimeClasspathTask extends DefaultTask
{
    private Configuration configuration;

    private File runtimeClasspathFile;

    @SuppressWarnings("unused")
    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    public Configuration getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration(Configuration configuration)
    {
        this.configuration = configuration;
    }

    @SuppressWarnings("unused")
    @OutputFile
    public File getRuntimeClasspathFile()
    {
        return runtimeClasspathFile;
    }

    public void setRuntimeClasspathFile(File runtimeClasspathFile)
    {
        this.runtimeClasspathFile = runtimeClasspathFile;
    }

    @TaskAction
    public void exec()
    {
        if (configuration == null || runtimeClasspathFile == null)
            throw new GradleException("Insufficient parameters: projects=" + configuration + " runtimeClasspathFile=" + runtimeClasspathFile);

        try
        {
            Path targetPath = runtimeClasspathFile.toPath();
            Files.createDirectories(targetPath.getParent());
            Files.write(targetPath, configuration.resolve().stream()
                                                 .sorted(Comparator.comparing(File::getName))
                                                 .distinct()
                                                 .map(File::getAbsolutePath)
                                                 .collect(Collectors.joining(File.pathSeparator))
                                                 .getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            throw new GradleException("Failed to write " + runtimeClasspathFile, e);
        }

        getProject().getLogger().info("Wrote {}", runtimeClasspathFile);
    }
}
