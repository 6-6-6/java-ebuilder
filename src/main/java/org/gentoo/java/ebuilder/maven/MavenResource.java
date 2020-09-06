package org.gentoo.java.ebuilder.maven;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
     * The dirname of resources, relative to workdir.
     */
    private Path originDirectory;

    /**
     * The dirname of the target directory of the resources,
     * relative to the root of the Java classes.
     */
    private Path targetDirectory;

    /**
     * The files that will be copied from {@link #originDirectory}
     * to {@link #targetDirectory}.
     */
    private final List<Path> includedFiles = new ArrayList<>(10);

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
    public MavenResource(Path originDirectory) {
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
    public MavenResource(Path originDirectory, Path targetDirectory) {
        if (isValidResourcesDir(originDirectory)) {
            this.originDirectory = originDirectory;
        } else {
            this.originDirectory = null;
        }
        this.targetDirectory = targetDirectory;
    }

    /**
     * Creates new instance, sets {origin,target}Directory,
     * and sets includedFiles.
     *
     * @param originDirectory {@link #originDirectory}
     * @param targetDirectory {@link #targetDirectory}
     * @param includedFiles {@link #includedFiles}
     */
    public MavenResource(Path originDirectory,
            Path targetDirectory, Collection<Path> includedFiles) {
        if (isValidResourcesDir(originDirectory)) {
            this.originDirectory = originDirectory;
        } else {
            this.originDirectory = null;
        }
        this.targetDirectory = targetDirectory;
        this.includedFiles.addAll(includedFiles);
    }

    /**
     * Setter for {@link #originDirectory}.
     *
     * @param originDirectory {@link #originDirectory}
     */
    public void setOriginDirectory(Path originDirectory) {
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
    public void setTargetDirectory(Path targetDirectory) {
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
     * Add a path to {@link #includedFiles}.
     *
     * @param includedFile path to the file
     */
    public void addIncludedFiles(Path includedFile) {
        if (isValidResourcesFile(Paths.get(
                originDirectory.toString(),
                includedFile.toString()))) {
            this.includedFiles.add(includedFile);
        }
    }

    /**
     * Add a collection of pathes to {@link #includedFiles}.
     *
     * @param includedFiles Collection<Path>
     */
    public void addIncludedFiles(Collection<Path> includedFiles) {
        for (Path includedFile : includedFiles) {
            addIncludedFiles(includedFile);
        }
    }

    /**
     * Getter for {@link #includedFiles}.
     *
     * @return {@link #includedFiles}
     */
    public List<Path> getIncludedFiles() {
        return Collections.unmodifiableList(includedFiles);
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
     *         "originDir" if targetDir and includedFiles are empty,
     *         "originDir:targetDir" if includedFiles is empty,
     *         "originDir:targetDir:includedFile1|File2" otherwise.
     */
    public String toString(final Config config) {
        final String resourceDir;

        if (originDirectory == null) {
            resourceDir = null;
        } else if (targetDirectory == null
                && includedFiles.isEmpty()) {
            resourceDir = config.getWorkdir().relativize(originDirectory).
                    toString();
        } else if (includedFiles.isEmpty()) {
            resourceDir = config.getWorkdir().relativize(originDirectory).
                    toString() + ":"
                    + targetDirectory.toString();
        } else {
            if (targetDirectory == null) {
                targetDirectory = Paths.get("");
            }
            final List<String> includedFileList = includedFiles.stream().
                    map(s -> s.toString()).
                    collect(Collectors.toList());
            final SortedSet<String> includedFileSet =
                    new TreeSet<String>(includedFileList);

            resourceDir = config.getWorkdir().relativize(originDirectory).
                    toString() + ":"
                    + targetDirectory.toString() + ":"
                    + String.join("|", includedFileSet);
        }

        return resourceDir;
    }
}
