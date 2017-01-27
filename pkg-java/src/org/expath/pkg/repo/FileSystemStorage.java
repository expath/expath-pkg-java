/****************************************************************************/
/*  File:       FileSystemStorage.java                                      */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2010-10-07                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo;

import java.io.*;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import javax.xml.transform.stream.StreamSource;
import org.expath.pkg.repo.tools.Logger;
import org.expath.pkg.repo.tools.PackagesTxtFile;
import org.expath.pkg.repo.tools.PackagesXmlFile;

/**
 * Storage using the file system.
 *
 * @author Florent Georges
 */
public class FileSystemStorage
        extends Storage
{
    public FileSystemStorage(File root)
            throws PackageException
    {
        // the repository root directory
        if ( root == null ) {
            throw new NullPointerException("The repository root directory is null");
        }
        if ( ! root.exists() ) {
            String msg = "The repository root directory does not exist: " + root;
            throw new PackageException(msg);
        }
        if ( ! root.isDirectory() ) {
            String msg = "The repository root directory is not a directory: " + root;
            throw new PackageException(msg);
        }
        myRoot = root;
        File dir = new File(root, ".expath-pkg");
        FileHelper.ensureDir(dir);
        myPrivate = dir;
        File xmlfile = new File(dir, "packages.xml");
        myXmlFile = new PackagesXmlFile(xmlfile);
        File txtfile = new File(dir, "packages.txt");
        myTxtFile = new PackagesTxtFile(txtfile);
    }

    public File getRootDirectory()
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
        File pkg_root = rsrc_name == null ? null : new File(myRoot, rsrc_name);
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
    public File makeTempDir(String prefix)
            throws PackageException
    {
        return FileHelper.makeTempDir(prefix, myPrivate);
    }

    @Override
    public boolean packageKeyExists(String key)
            throws PackageException
    {
        File f = new File(myRoot, key);
        return f.exists();
    }

    @Override
    public void storeInstallDir(File dir, String key, Package pkg)
            throws PackageException
    {
        // move the temporary dir content to the repository
        File dest = new File(myRoot, key);
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
        File txt_file = new File(myPrivate, "packages.txt");
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
        FileHelper.deleteQuietly(resolver.myPkgDir.toPath());
    }

    @Override
    public String toString()
    {
        return "File system storage in " + myRoot.getAbsolutePath();
    }

    /**
     * If true (the default), an error is thrown if there is no content dir in the package.
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

    /**
     * Delete a complete directory (with its descendants).
     */
    private void deleteDirRecurse(File target)
            throws PackageException
    {
        // recurse if needed
        File[] children = target.listFiles();
        if ( children != null ) {
            for ( File child : children ) {
                deleteDirRecurse(child);
            }
        }
        // delete target (if it is a dir, it is empty after recursing)
        for ( int i = 3; i >= 0; --i ) {
            boolean success = shallowDelete(target, i);
            if ( success ) {
                break;
            }
        }
    }

    /**
     * Delete a file or a directory.
     * 
     * @param target The file or directory to delete. The function does not
     * recurse, so if {@code target} is a directory, it must be empty.
     * 
     * @param remain The remaining attempts at trying. If it is 0, this function
     * throws an error if the deletion fails.
     * 
     * @throws PackageException If {@code remain} is 0 and deleting the target
     * fails.
     */
    @SuppressWarnings("SleepWhileInLoop")
    private boolean shallowDelete(File target, int remain)
            throws PackageException
    {
        if ( ! target.exists() ) {
            // trying to delete a non-existing file or dir
            return true;
        }
        boolean success = target.delete();
        if ( success ) {
            return true;
        }
        // because of Windows file management, we need to try to collect the
        // garbage before failing, in case deleting a file fails
        // TODO: Do it only on Windows?
        // TODO: Is there a better way?
        System.gc();
        try {
            Thread.sleep(100);
        }
        catch ( InterruptedException ex ) {
            throw new PackageException("Interrupted while deleting: " + target);
        }
        if ( remain <= 0 ) {
            if ( target.isDirectory() ) {
                throw new PackageException("Error deleting a dir: " + target);
            }
            else {
                throw new PackageException("Error deleting a file: " + target);
            }
        }
        return false;
    }

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(FileSystemStorage.class);

    /** The root dir of the repo. */
    private File myRoot;
    /** The private area.  Must be used only through getPrivateFile(). */
    private File myPrivate;
    /** The package list, XML format, in [repo]/.expath-pkg/packages.xml. */
    private final PackagesXmlFile myXmlFile;
    /** The package list, text format, in [repo]/.expath-pkg/packages.txt. */
    private final PackagesTxtFile myTxtFile;
    /** Throw an error if none content dir exist? */
    private boolean myErrorIfNoContentDir = true;

    public class FileSystemResolver
            extends PackageResolver
    {
        public FileSystemResolver(File pkg_dir, String abbrev, String rsrc_name)
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
            return myContentDir.toURI();
        }

        private void setPkgDir(File dir)
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

        private File getContenDir(File pkg_dir, String abbrev)
                 throws PackageException
        {
            File old_style = new File(pkg_dir, abbrev);
            File new_style = new File(pkg_dir, "content");
            boolean old_exists = old_style.exists();
            boolean new_exists = new_style.exists();
            boolean old_isdir = old_style.isDirectory();
            boolean new_isdir = new_style.isDirectory();
            LOG.finer("Content dir ''{0}'' (exists:{1}/isdir:{2}), and ''{3}'' (exists:{4}/isdir:{5})",
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
                LOG.info("Warning: package uses old-style content dir: ''{0}''", old_style);
                return old_style;
            }
        }

        public File resolveResourceAsFile(String path)
        {
            return new File(myPkgDir, path);
        }

        public File resolveComponentAsFile(String path)
        {
            if ( myContentDir == null ) {
                return null;
            }
            return new File(myContentDir, path);
        }

        @Override
        public StreamSource resolveResource(String path)
                throws PackageException
                     , NotExistException
        {
            return resolveWithin(path, myPkgDir);
        }

        @Override
        public StreamSource resolveComponent(String path)
                throws PackageException
                     , NotExistException
        {
            if ( myContentDir == null ) {
                return null;
            }
            return resolveWithin(path, myContentDir);
        }

        private StreamSource resolveWithin(String path, File dir)
                throws PackageException
                     , NotExistException
        {
            LOG.fine("Trying to resolve ''{0}'' within ''{1}''", path, dir);
            File f = new File(dir, path);
            if ( ! f.exists() ) {
                String msg = "File '" + f + "' does not exist";
                LOG.fine(msg);
                throw new NotExistException(msg);
            }
            try {
                InputStream in = new FileInputStream(f);
                StreamSource src = new StreamSource(in);
                src.setSystemId(f.toURI().toString());
                return src;
            }
            catch ( FileNotFoundException ex ) {
                String msg = "File '" + f + "' exists but is not found";
                LOG.severe(msg);
                throw new PackageException(msg, ex);
            }
        }

        private String getDirName()
        {
            return myPkgDir.getName();
        }

        private final String myRsrcName;
        private final String myPkgAbbrev;
        private File         myPkgDir;
        private File         myContentDir;
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
