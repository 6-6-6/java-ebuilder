package org.gentoo.java.ebuilder.maven;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.SortedSet;
import java.util.TreeSet;
import org.gentoo.java.ebuilder.Config;

/**
 * Describes the "resource" element of pom.xml
 *
 * @author Zhang Zongyu
 */
public class MavenResource {

    /**
     * Whether the {@link #resourceFiles} are files that will be
     * included or excluded while packaging:
     * 0 for including;
     * 1 for excluding.
     */
    private int action = 0;

    /**
     * Whether there are variables needs to be replaced by values.
     */
    private boolean filtering = false;

    /**
     * The files that will be copied from {@link #originDirectory}
     * to {@link #targetDirectory}.
     */
    private final SortedSet<String> resourceFiles = new TreeSet<>();

    /**
     * The dirname of resources, relative to workdir.
     */
    private Path originDirectory;

    /**
     * The dirname of the target directory of the resources,
     * relative to the root of the Java classes.
     */
    private Path targetDirectory;

    /**
     * Creates new instance.
     */
    public MavenResource() {
        this.originDirectory = null;
        this.targetDirectory = null;
    }

    /**
     * Creates new instance, and sets originDirectory.
     *
     * @param originDirectory {@link #originDirectory}
     */
    public MavenResource(final Path originDirectory) {
        if (isValidResourcesDir(originDirectory)) {
            this.originDirectory = originDirectory;
        } else {
            this.originDirectory = null;
        }
        this.targetDirectory = null;
    }

    /**
     * Creates new instance, and sets {origin,target}Directory.
     *
     * @param originDirectory {@link #originDirectory}
     * @param targetDirectory {@link #targetDirectory}
     */
    public MavenResource(final Path originDirectory,
            final Path targetDirectory) {
        if (isValidResourcesDir(originDirectory)) {
            this.originDirectory = originDirectory;
        } else {
            this.originDirectory = null;
        }
        this.targetDirectory = targetDirectory;
    }

    /**
     * Creates new instance, sets {origin,target}Directory,
     * and sets resourceFiles.
     *
     * @param originDirectory {@link #originDirectory}
     * @param targetDirectory {@link #targetDirectory}
     * @param resourceFiles {@link #resourceFiles}
     */
    public MavenResource(final Path originDirectory,
            final Path targetDirectory, Collection<String> resourceFiles) {
        if (isValidResourcesDir(originDirectory)) {
            this.originDirectory = originDirectory;
        } else {
            this.originDirectory = null;
        }
        this.targetDirectory = targetDirectory;
        addResourceFiles(resourceFiles);
    }

    /**
     * Setter for {@link #action}.
     *
     * @param action {@link #action}
     */
    public void setAction(final int action) {
        this.action = action;
    }

    /**
     * Getter for {@link #action}.
     *
     * @return {@link #action}
     */
    public int getAction() {
        return action;
    }

    /**
     * Setter for {@link #filtering}.
     *
     * @param filtering {@link #filtering}
     */
    public void setFiltering(final boolean filtering) {
        this.filtering = filtering;
    }

    /**
     * Getter for {@link #filtering}.
     *
     * @return {@link #filtering}
     */
    public boolean getFiltering() {
        return filtering;
    }

    /**
     * Add a path to {@link #resourceFiles}.
     *
     * @param resourceFile path to the file
     */
    public void addResourceFiles(final String resourceFile) {
        if (resourceFile.contains("*")
                || isValidResourcesFile(
                Paths.get(originDirectory.toString(), resourceFile))) {
            this.resourceFiles.add(resourceFile);
        }
    }

    /**
     * Add a collection of pathes to {@link #resourceFiles}.
     *
     * @param resourceFiles Collection<Path>
     */
    public void addResourceFiles(Collection<String> resourceFiles) {
        for (String resourceFile : resourceFiles) {
            addResourceFiles(resourceFile);
        }
    }

    /**
     * Getter for {@link #resourceFiles}.
     *
     * @return {@link #resourceFiles}
     */
    public SortedSet<String> getResourceFiles() {
        return Collections.unmodifiableSortedSet(resourceFiles);
    }

    /**
     * Setter for {@link #originDirectory}.
     *
     * @param originDirectory {@link #originDirectory}
     */
    public void setOriginDirectory(final Path originDirectory) {
        if (isValidResourcesDir(originDirectory)) {
            this.originDirectory = originDirectory;
        }
    }

    /**
     * Getter for {@link #originDirectory}.
     *
     * @return {@link #originDirectory}
     */
    public Path getOriginDirectory() {
        return originDirectory;
    }

    /**
     * Setter for {@link #targetDirectory}.
     *
     * @param targetDirectory {@link #targetDirectory}
     */
    public void setTargetDirectory(final Path targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    /**`
     * Getter for {@link #targetDirectory}.
     *
     * @return {@link #targetDirectory}
     */
    public Path getTargetDirectory() {
        return targetDirectory;
    }

    /**
     * Checks whether the provided path is a valid directory for resources. It
     * must exist and contain at least one file.
     *
     * @param resources path to resources
     *
     * @return true if the resources directory is valid, otherwise false
     */
    private boolean isValidResourcesDir(final Path resources) {
        return resources.toFile().exists()
                && resources.toFile().list().length != 0;
    }

    /**
     * Checks whether the provided path is a valid file.
     *
     * @param resources path to resources
     *
     * @return true if the resourcesfile is valid, otherwise false
     */
    private boolean isValidResourcesFile(final Path resources) {
        return resources.toFile().isFile();
    }

    /**
     * format the output to satisfy java-pkg-simple.eclass
     *
     * @return null if the origin dir does not exits,
     *         "originDir" if targetDir and resourceFiles are empty,
     *         "originDir:targetDir" if resourceFiles is empty,
     *         "originDir:targetDir:File1|File2" otherwise.
     */
    public String toString(final Config config) {
        final String resourceDir;

        if (filtering) {
            config.getStdoutWriter().println("\n###############################");
            config.getStdoutWriter().println("ATTENTION:");
            config.getStdoutWriter().println(
                "This pom.xml attempts to use 'filtering' feature of Maven.");
            config.getStdoutWriter().println("Unfortunately, it is not "
            + "yet supported by java-ebuilder and java-pkg-simple.eclass");
            config.getStdoutWriter().println("Consider handle it manually.");
            config.getStdoutWriter().println("###############################");
        }

        if (originDirectory == null) {
            resourceDir = null;
        } else if (targetDirectory == null
                && resourceFiles.isEmpty()) {
            resourceDir = config.getWorkdir().relativize(originDirectory).
                    toString();
        } else if (resourceFiles.isEmpty()) {
            resourceDir = config.getWorkdir().relativize(originDirectory).
                    toString() + ":"
                    + targetDirectory.toString();
        } else {
            if (targetDirectory == null) {
                targetDirectory = Paths.get("");
            }

            switch (action) {
                case 0:
                    resourceDir = config.getWorkdir().relativize(originDirectory).
                            toString() + ":"
                            + targetDirectory.toString() + ":"
                            + String.join("|", resourceFiles);
                    break;
                case 1:
                    resourceDir = config.getWorkdir().relativize(originDirectory).
                            toString() + ":"
                            + targetDirectory.toString() + ":"
                            + "!" + String.join("|!", resourceFiles);
                    break;
                default:
                    resourceDir = null;
            }
        }

        return resourceDir;
    }
}
