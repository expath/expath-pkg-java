/****************************************************************************/
/*  File:       PackagesXmlFile.java                                        */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2013-09-17                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2013 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo.tools;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import javax.xml.transform.Transformer;
import org.expath.pkg.repo.Package;
import org.expath.pkg.repo.PackageException;

/**
 * Represent the file [repo]/.expath-pkg/packages.xml.
 *
 * @author Florent Georges
 */
public class PackagesXmlFile
        extends UpdatableXmlFile
{
    /**
     * Create a new instance.
     * 
     * If the file does not exist yet, it is created.
     * 
     * @param file The actual file, for [repo]/.expath-pkg/packages.xml.
     * 
     * @throws PackageException If any error when creating the file (if it does
     *     not exist yet).
     */
    public PackagesXmlFile(Path file)
            throws PackageException
    {
        super(file);
    }

    /**
     * Add a package to packages.xml.
     * 
     * @param pkg The package to add.
     * 
     * @param dir The name of the directory where the package is installed,
     *     right below the repository root.
     * 
     * @throws PackageException In case of any error (mostly related to I/Os).
     */
    public void addPackage(Package pkg, String dir)
            throws PackageException
    {
        Transformer trans = compile(ADD_PACKAGE_XSL);
        trans.setParameter("name",    pkg.getName());
        trans.setParameter("dir",     dir);
        trans.setParameter("version", pkg.getVersion());
        transform(trans);
    }

    /**
     * Remove a package from packages.xml.
     * 
     * The package is identified by its directory name (the name of the
     * directory where it is installed, right below the repository root).
     * 
     * @param dir The directory to remove from the packages file.
     * 
     * @throws PackageException In case of any error transforming the packages
     *     file.
     */
    public void removePackageByDir(String dir)
            throws PackageException
    {
        Transformer trans = compile(REMOVE_PACKAGE_XSL);
        trans.setParameter("dir", dir);
        transform(trans);
    }

    @Override
    protected void createEmpty(Writer out)
            throws IOException
    {
        out.write("<packages xmlns=\"http://expath.org/ns/repo/packages\"/>\n");
    }

    /** The stylesheet to add a package, as a Java resource name. */
    private static final String ADD_PACKAGE_XSL    = "org/expath/pkg/repo/rsrc/add-package.xsl";
    /** The stylesheet to remove a package, as a Java resource name. */
    private static final String REMOVE_PACKAGE_XSL = "org/expath/pkg/repo/rsrc/remove-package.xsl";
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
