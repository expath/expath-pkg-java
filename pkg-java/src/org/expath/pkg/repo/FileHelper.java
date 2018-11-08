/****************************************************************************/
/*  File:       FileHelper.java                                             */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2011-04-14                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2011 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo;

import org.expath.pkg.repo.tools.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Centralize some file-related utilities.
 *
 * @author Florent Georges
 */
public class FileHelper
{
    /**
     * Create a directory if it does not exist yet.
     * 
     * Throw an error if the directory already exists and is not actually a
     * directory, or if there is an error creating it.
     */
    public static void ensureDir(Path dir)
            throws PackageException
    {
        // if it does not exist, create it
        if ( ! Files.exists(dir) ) {
            try {
                Files.createDirectories(dir);
            } catch (final IOException e) {
                throw new PackageException("Error creating the directory: " + dir, e);
            }
        }
        // but if it exists and is not a dir, that's an error
        else if ( ! Files.isDirectory(dir) ) {
            throw new PackageException("The directory is not a directory: " + dir);
        }
    }

    /**
     * Create a new directory within {@code parent}.
     * 
     * The new directory is empty, and its name is based on {@code prefix},
     * and on the current date after the prefix.
     */
    public static Path makeTempDir(final String prefix, final Path parent)
            throws PackageException
    {
        Date now = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        String today = fmt.format(now);
        try {
            return Files.createTempDirectory(parent, prefix + "-" + today + "-");
        }
        catch ( IOException ex ) {
            throw new PackageException("Error creating a temporary file", ex);
        }
    }

    public static void renameTmpDir(Path from, Path to)
            throws PackageException
    {
        try {
            Files.move(from, to);
        } catch (final IOException e) {
            // if renaming failed and we're not on Windows, that's an error
            // if renaming failed and we're on Windows, we try several things, as
            // this can be related to tricky Windows locking handling...
            if (!System.getProperty("os.name").startsWith("Windows") ) {
                throw new PackageException("Renaming '" + from + "' to '" + to + "' failed", e);
            }

            // windows - then try to copy the whole thing
            try {
                copy(from, to);
            } catch (final IOException ioe) {
                throw new PackageException("Copying '" + from + "' to '" + to + "' failed", ioe);
            }

            boolean res = deleteQuietly(from);
            if ( ! res ) {
                System.err.println("Error deleting dir: " + from);
            }
        }
    }

    /**
     * Copies a path within the filesystem
     *
     * If the path is a directory its contents
     * will be recursively copied.
     *
     * Note that copying of a directory is not an atomic-operation
     * and so if an error occurs during copying, some of the directories
     * descendants may have already been copied.
     *
     * @param source the source file or directory
     * @param destination the destination file or directory
     *
     * @throws IOException if an error occurs whilst copying a file or directory
     */
    public static void copy(final Path source, final Path destination) throws IOException {
        if (!Files.isDirectory(source)) {
            Files.copy(source, destination);
        } else {
            if (Files.exists(destination) && !Files.isDirectory(destination)) {
                throw new IOException("Cannot copy a directory to a file");
            }
            Files.walkFileTree(source, copyDirVisitor(source, destination));
        }
    }

    private final static SimpleFileVisitor<Path> copyDirVisitor(final Path source, final Path destination) throws IOException {
        if (!Files.isDirectory(source)) {
            throw new IOException("source must be a directory");
        }
        if (!Files.isDirectory(destination)) {
            throw new IOException("destination must be a directory");
        }
        return new CopyDirVisitor(source, destination);
    }

    private static class CopyDirVisitor extends SimpleFileVisitor<Path> {
        private final Path source;
        private final Path destination;

        public CopyDirVisitor(final Path source, final Path destination) {
            this.source = source;
            this.destination = destination;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            final Path relSourceDir = source.relativize(dir);
            final Path targetDir = destination.resolve(relSourceDir);
            Files.createDirectories(targetDir);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            final Path relSourceFile = source.relativize(file);
            final Path targetFile = destination.resolve(relSourceFile);
            Files.copy(file, targetFile);
            return FileVisitResult.CONTINUE;
        }
    }

    public static boolean deleteQuietly(final Path path) {
        try {
            if (!Files.isDirectory(path)) {
                return Files.deleteIfExists(path);
            } else {
                Files.walkFileTree(path, deleteDirVisitor);
            }
            return true;
        } catch (final IOException ioe) {
            LOG.info("Failed to delete " + path.toString(), ioe);
            return false;
        }
    }

    private final static SimpleFileVisitor<Path> deleteDirVisitor = new DeleteDirVisitor();

    private static class DeleteDirVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Files.deleteIfExists(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            if (exc != null) {
                throw exc;
            }

            Files.deleteIfExists(dir);
            return FileVisitResult.CONTINUE;
        }
    }

    /**
     * A list of the entries in the directory. The listing is not recursive.
     *
     * @param directory The directory to list the entries for
     *
     * @return The list of entries
     */
    public static List<Path> list(final Path directory) throws IOException {
        try(final Stream<Path> entries = Files.list(directory)) {
            return entries.collect(Collectors.toList());
        }
    }

    public static boolean isEmpty(final Path directory) {
        try(final Stream<Path> entries = Files.list(directory)) {
            return !entries.findFirst().isPresent();
        } catch (final IOException e) {
            return true;
        }
    }

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(FileHelper.class);
}

/* ------------------------------------------------------------------------ */
/*  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS COMMENT.               */
/*                                                                          */
/*  The contents of this file are subject to the Mozilla Public License     */
/*  Version 1.0 (the "License"); you may not use this file except in        */
/*  compliance with the License. You may obtain a copy of the License at    */
/*  http://www.mozilla.org/MPL/.                                            */
/*                                                                          */
/*  Software distributed under the License is distributed on an "AS IS"     */
/*  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See    */
/*  the License for the specific language governing rights and limitations  */
/*  under the License.                                                      */
/*                                                                          */
/*  The Original Code is: all this file.                                    */
/*                                                                          */
/*  The Initial Developer of the Original Code is Florent Georges.          */
/*                                                                          */
/*  Contributor(s): none.                                                   */
/* ------------------------------------------------------------------------ */
