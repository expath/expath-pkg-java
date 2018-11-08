/****************************************************************************/
/*  File:       Storage.java                                                */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2010-10-07                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo;

import java.net.URI;
import java.nio.file.Path;
import java.util.Set;
import javax.xml.transform.Source;

/**
 * Abstract the physical storage of a repository.
 *
 * For instance, two standard implementations are on disk (the official layout
 * defined in the spec) and within the classpath.  The former resolves resource
 * names as filenames on the disk, the later resolves them as resource names
 * within the classpath.
 *
 * @author Florent Georges
 */
public abstract class Storage
{
    /**
     * Return whether this storage is read-only.
     */
    public abstract boolean isReadOnly();

    /**
     * Return a resolver for a specific package.
     *
     * {@code rsrc_name} is the name of the resource representing the package.
     * For instance, on the filesystem that is the name of the root directory,
     * and in the classpath that is the name of the root package (in the Java
     * sense).
     *
     * {@code abbrev} is the abbrev of the package (as in the package descriptor,
     * and must match the module dir within the package).
     */
    public abstract PackageResolver makePackageResolver(String rsrc_name, String abbrev)
            throws PackageException;

    /**
     * Return the list of installed packages.
     *
     * The returned list is the list of the package directories within the
     * repository.
     */
    public abstract Set<String> listPackageDirectories()
            throws PackageException;

    /**
     * The opportunity to do anything before the install of a package.
     * 
     * If installation is not supported (because the storage is read-only),
     * this method must throw an exception.
     */
    public abstract void beforeInstall(boolean force, UserInteractionStrategy interact)
            throws PackageException;

    /**
     * TODO: ...
     */
    public abstract Path makeTempDir(String prefix)
            throws PackageException;

    /**
     * Each package in a repository can be identified by a unique key.
     * 
     * The key is computed based on the package abbrev, and is unique.
     * Typically it is used to create a directory or a collection, which
     * requires to have a unique string without '/'.
     */
    public abstract boolean packageKeyExists(String key)
            throws PackageException;

    /**
     * Actually store the package in the storage.
     * 
     * During install, the package is unzipped in a temporary directory on the
     * file system.  This method receives that directory and the package key to
     * use, and actually take control over the temporary directory.
     * 
     * It can rename the directory to its final place (does not need to copy
     * it). If it is not using the temporary directory anymore, it must delete
     * it.
     */
    public abstract void storeInstallDir(Path dir, String key, Package pkg)
            throws PackageException;

    /**
     * The package has just been install, record the information if needed.
     */
    public abstract void updatePackageLists(Package pkg)
            throws PackageException;

    /**
     * TODO: ...
     */
    public abstract void remove(Package pkg)
            throws PackageException;

    /**
     * Resolve paths in a package installed in the repository.
     */
    public static abstract class PackageResolver
    {
        /**
         * @return the package ID within the repository (i.e. its subdirectory name).
         */
        public abstract String getResourceName();

        /**
         * Resolve a resource within the "package directory".
         * 
         * @param path The path to resolve, relative to the package directory.
         * 
         * @return the corresponding {@link Source} object.
         * 
         * @throws NotExistException if the corresponding resource does not exist
         * in the package.
         * 
         * @throws PackageException in case of any other error.
         */
        public abstract Source resolveResource(String path)
                throws PackageException
                     , NotExistException;

        /**
         * Resolve a resource within the package "content directory".
         * 
         * @param path The path to resolve, relative to the package content
         * directory.
         * 
         * @return the corresponding {@link Source} object.
         * 
         * @throws NotExistException if the corresponding component does not exist
         * in the package.
         * 
         * @throws PackageException in case of any other error.
         */
        public abstract Source resolveComponent(String path)
                throws PackageException
                     , NotExistException;

        /**
         * The base URI of the package "content directory".
         * 
         * It is not recommended that the user resolves URIs him- or herself.
         * Other functions in this class are all about resolving resources and
         * components in this package.  But sometimes, the user will want to
         * simply get back the resolved URI, instead of the corresponding entity
         * content.
         * 
         * Using {@link URI#resolve(String)} on the returned value, with a
         * relative filename, must resolve this filename in the installed package
         * "content directory".
         * 
         * Use with caution, especially on storages not on a (regular) file-system.
         * 
         * @return the base URI of the package "content directory".
         * 
         * @throws PackageException in case of any error. Also thrown by storage
         * implementations for which such a URI does not make sense.
         */
        public abstract URI getContentDirBaseURI()
                throws PackageException;
    }

    /**
     * If a resource or a component does not exist, when trying to resolve a path.
     */
    public static class NotExistException
            extends Exception
    {
        public NotExistException(String msg)
        {
            super(msg);
        }

        public NotExistException(String msg, Throwable ex)
        {
            super(msg, ex);
        }
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
