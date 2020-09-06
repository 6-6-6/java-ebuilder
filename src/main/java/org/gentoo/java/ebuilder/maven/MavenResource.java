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
        this.originDirectory = Paths.get("");
        this.targetDirectory = Paths.get("");
    }

    /**
     * Creates new instance, and sets originDirectory.
     *
     * @param originDirectory {@link #originDirectory}
     */
    public MavenResource(Path originDirectory) {
        this.originDirectory = originDirectory;
        this.targetDirectory = Paths.get("");
    }

    /**
     * Creates new instance, and sets {origin,target}Directory.
     *
     * @param originDirectory {@link #originDirectory}
     * @param targetDirectory {@link #targetDirectory}
     */
    public MavenResource(Path originDirectory, Path targetDirectory) {
        this.originDirectory = originDirectory;
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
        this.originDirectory = originDirectory;
        this.targetDirectory = targetDirectory;
        this.includedFiles.addAll(includedFiles);
    }

    /**
     * Setter for {@link #originDirectory}.
     *
     * @param originDirectory {@link #originDirectory}
     */
    public void setOriginDirectory(Path originDirectory) {
        this.originDirectory = originDirectory;
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

    /**
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
     * @param Path
     */
    public void addIncludedFiles(Path includedFile) {
        this.includedFiles.add(includedFile);
    }

    /**
     * Add a collection of pathes to {@link #includedFiles}.
     *
     * @param Collection<Path>
     */
    public void addIncludedFiles(Collection<Path> includedFiles) {
        this.includedFiles.addAll(includedFiles);
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
     * format the output to satisfy java-pkg-simple.eclass
     *
     * @return "originDir" if targetDir and includedFiles are empty,
     *         "originDir:targetDir" if includedFiles is empty,
     *         "originDir:targetDir:includedFile1|File2" otherwise.
     */
    public String toString() {
        final String resourceDir;

        if (targetDirectory.toString() == ""
                && includedFiles.isEmpty()) {
            resourceDir = originDirectory.toString();
        } else if (includedFiles.isEmpty()) {
            resourceDir = originDirectory.toString() + ":"
                    + targetDirectory.toString();
        } else {
            final List<String> includedFileList = includedFiles.stream().
                    map(s -> s.toString()).
                    collect(Collectors.toList());
            final SortedSet<String> includedFileSet = new TreeSet<String>(includedFileList);

            resourceDir = originDirectory.toString() + ":"
                    + targetDirectory.toString() + ":"
                    + String.join("|", includedFileSet);
        }

        return resourceDir;
    }
}
