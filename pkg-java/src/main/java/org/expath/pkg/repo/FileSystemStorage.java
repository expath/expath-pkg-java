/****************************************************************************/
/*  File:       FileSystemStorage.java                                      */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2010-10-07                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import javax.annotation.Nullable;
import javax.xml.transform.stream.StreamSource;
import org.expath.pkg.repo.tools.PackagesTxtFile;
import org.expath.pkg.repo.tools.PackagesXmlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Storage using the file system.
 *
 * @author Florent Georges
 */
public class FileSystemStorage
        extends Storage
{
    public FileSystemStorage(Path root)
            throws PackageException
    {
        // the repository root directory
        if ( root == null ) {
            throw new NullPointerException("The repository root directory is null");
        }
        if ( ! Files.exists(root) ) {
            String msg = "The repository root directory does not exist: " + root;
            throw new PackageException(msg);
        }
        if ( ! Files.isDirectory(root) ) {
            String msg = "The repository root directory is not a directory: " + root;
            throw new PackageException(msg);
        }
        myRoot = root;
        Path dir = root.resolve(".expath-pkg");
        FileHelper.ensureDir(dir);
        myPrivate = dir;
        Path xmlfile = dir.resolve("packages.xml");
        myXmlFile = new PackagesXmlFile(xmlfile);
        Path txtfile = dir.resolve("packages.txt");
        myTxtFile = new PackagesTxtFile(txtfile);
    }

    public Path getRootDirectory()
    {
        return myRoot;
    }

    @Override
    public boolean isReadOnly()
    {
        return false;
    }

    @Override
    public PackageResolver makePackageResolver(String rsrc_name, String abbrev)
                 throws PackageException
    {
        Path pkg_root = rsrc_name == null ? null : myRoot.resolve(rsrc_name);
        return new FileSystemResolver(pkg_root, abbrev, rsrc_name);
    }

    @Override
    public Set<String> listPackageDirectories()
            throws PackageException
    {
        return myTxtFile.parseDirectories();
    }

    @Override
    public void beforeInstall(boolean force, UserInteractionStrategy interact)
            throws PackageException
    {
        // nothing
    }

    @Override
    public Path makeTempDir(String prefix)
            throws PackageException
    {
        return FileHelper.makeTempDir(prefix, myPrivate);
    }

    @Override
    public boolean packageKeyExists(String key)
            throws PackageException
    {
        Path f = myRoot.resolve(key);
        return Files.exists(f);
    }

    @Override
    public void storeInstallDir(Path dir, String key, Package pkg)
            throws PackageException
    {
        // move the temporary dir content to the repository
        Path dest = myRoot.resolve(key);
        FileHelper.renameTmpDir(dir, dest);
        FileSystemResolver resolver = getResolver(pkg);
        resolver.setPkgDir(dest);
    }

    @Override
    public void updatePackageLists(Package pkg)
            throws PackageException
    {
        FileSystemResolver resolver = getResolver(pkg);
        String dir = resolver.getDirName();
        myXmlFile.addPackage(pkg, dir);
        Path txt_file = myPrivate.resolve("packages.txt");
        myTxtFile.addPackage(pkg, dir);
    }

    @Override
    public void remove(Package pkg)
            throws PackageException
    {
        FileSystemResolver resolver = getResolver(pkg);
        // remove the entries from the packages.* files
        String dir = resolver.getDirName();
        myXmlFile.removePackageByDir(dir);
        myTxtFile.removePackageByDir(dir);
        // actually delete the files
//        deleteDirRecurse(resolver.myPkgDir);
        FileHelper.deleteQuietly(resolver.myPkgDir);
    }

    @Override
    public String toString()
    {
        return "File system storage in " + myRoot.toAbsolutePath();
    }

    /**
     * If true (the default), an error is thrown if there is no content dir in the package.
     *
     * @param value the new value
     */
    public void setErrorIfNoContentDir(boolean value)
    {
        myErrorIfNoContentDir = value;
    }

    private FileSystemResolver getResolver(Package pkg)
            throws PackageException
    {
        Storage.PackageResolver base_resolver = pkg.getResolver();
        if ( ! (base_resolver instanceof FileSystemResolver) ) {
            throw new PackageException("The package has not been installed in this storage.");
        }
        return (FileSystemResolver) base_resolver;
    }

    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemStorage.class);

    /** The root dir of the repo. */
    private Path myRoot;
    /** The private area.  Must be used only through getPrivateFile(). */
    private Path myPrivate;
    /** The package list, XML format, in [repo]/.expath-pkg/packages.xml. */
    private final PackagesXmlFile myXmlFile;
    /** The package list, text format, in [repo]/.expath-pkg/packages.txt. */
    private final PackagesTxtFile myTxtFile;
    /** Throw an error if none content dir exist? */
    private boolean myErrorIfNoContentDir = true;

    public class FileSystemResolver
            extends PackageResolver
    {
        public FileSystemResolver(@Nullable final Path pkg_dir, final String abbrev, final String rsrc_name)
                 throws PackageException
        {
            myPkgAbbrev = abbrev;
            myRsrcName = rsrc_name;
            setPkgDir(pkg_dir);
        }

        @Override
        public String getResourceName()
        {
            return myRsrcName;
        }

        @Override
        public URI getContentDirBaseURI()
        {
            return myContentDir.toUri();
        }

        private void setPkgDir(@Nullable final Path dir)
                 throws PackageException
        {
            myPkgDir = dir;
            if ( dir == null || myPkgAbbrev == null ) {
                myContentDir = null;
            }
            else {
                myContentDir = getContenDir(dir, myPkgAbbrev);
            }
        }

        private Path getContenDir(Path pkg_dir, String abbrev)
                 throws PackageException
        {
            Path old_style = pkg_dir.resolve(abbrev);
            Path new_style = pkg_dir.resolve("content");
            boolean old_exists = Files.exists(old_style);
            boolean new_exists = Files.exists(new_style);
            boolean old_isdir = Files.isDirectory(old_style);
            boolean new_isdir = Files.isDirectory(new_style);
            LOG.trace("Content dir '{}' (exists:{}/isdir:{}), and '{}' (exists:{}/isdir:{})",
                    new_style, new_exists, new_isdir, old_style, old_exists, old_isdir);
            if ( ! old_exists && ! new_exists ) {
                String msg = "None of content dirs exist: '" + new_style + "' and '" + old_style + "'";
                LOG.info(msg);
                if ( myErrorIfNoContentDir ) {
                    throw new PackageException(msg);
                }
                return null;
            }
            else if ( old_exists && new_exists ) {
                String msg = "Both content dirs exist: '" + new_style + "' and '" + old_style + "'";
                LOG.info(msg);
                throw new PackageException(msg);
            }
            else if ( new_exists ) {
                if ( ! new_isdir ) {
                    String msg = "Content dir is not a directory: '" + new_style + "'";
                    LOG.info(msg);
                    throw new PackageException(msg);
                }
                return new_style;
            }
            else {
                if ( ! old_isdir ) {
                    String msg = "Content dir is not a directory: '" + old_style + "'";
                    LOG.info(msg);
                    throw new PackageException(msg);
                }
                LOG.info("Warning: package uses old-style content dir: '{}'", old_style);
                return old_style;
            }
        }

        public Path resolveResourceAsFile(String path)
        {
            return myPkgDir.resolve(path);
        }

        public Path resolveComponentAsFile(String path)
        {
            if ( myContentDir == null ) {
                return null;
            }
            return myContentDir.resolve(path);
        }

        @Override
        public StreamSource resolveResource(String path)
                throws PackageException
                     , NotExistException
        {
            return resolveWithin(path, myPkgDir);
        }

        @Override
        public StreamSource resolveComponent(final String path) throws PackageException, NotExistException {
            @Nullable StreamSource component = null;

            // first try the contentDir
            if (myContentDir != null) {
                component = tryResolveWithin(path, myContentDir);
            }

            if (component == null && myPkgDir != null) {
                // fallback to trying the pkgDir
                component = tryResolveWithin(path, myPkgDir);
            }

            if (component == null) {
                // throw a NotExistException
                final StringBuilder builder = new StringBuilder();
                builder.append( "Could not locate component '").append(path).append("'");
                if (myContentDir != null) {
                    builder.append(" in: ").append(myContentDir.normalize().toAbsolutePath());
                }
                if (myPkgDir != null) {
                    if (myContentDir != null) {
                        builder.append(", or: ");
                    } else {
                        builder.append(" in: ");
                    }
                    builder.append(myPkgDir.normalize().toAbsolutePath());
                }
                final String msg = builder.toString();
                LOG.debug(msg);
                throw new NotExistException(msg);
            }

            return component;
        }

        private StreamSource resolveWithin(final String path, final Path dir) throws PackageException, NotExistException {
            @Nullable final StreamSource result = tryResolveWithin(path, dir);
            if (result == null) {
                final String msg = "File '" + dir.resolve(path) + "' does not exist";
                LOG.debug(msg);
                throw new NotExistException(msg);
            }
            return result;
        }

        private @Nullable StreamSource tryResolveWithin(final String path, final Path dir) throws PackageException {
            LOG.debug("Trying to resolve '{}' within '{}'", path, dir);
            final Path f = dir.resolve(path);
            if (!Files.exists(f)) {
                return null;
            }

            try {
                final InputStream in = Files.newInputStream(f);
                final StreamSource src = new StreamSource(in);
                src.setSystemId(f.toUri().toString());
                return src;
            } catch (final IOException ex) {
                String msg = "File '" + f + "' exists but is not found";
                LOG.error(msg);
                throw new PackageException(msg, ex);
            }
        }

        private String getDirName()
        {
            return myPkgDir.getFileName().toString();
        }

        private final String myRsrcName;
        private final String myPkgAbbrev;
        @Nullable private Path myPkgDir;
        @Nullable private Path myContentDir;
    }
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
