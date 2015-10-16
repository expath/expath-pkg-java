/****************************************************************************/
/*  File:       Extension.java                                              */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2010-09-18                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo;

import java.util.Map;

/**
 * An extension to the packaging system.
 * 
 * This abstract class represents the various callback points where a specific
 * extension can perform steps specific to the extension, both to initialize a
 * {@link Package} object in memory, and to store additional information in the
 * repository at install time.
 *
 * @author Florent Georges
 */
public abstract class Extension
{
    public Extension(String name)
    {
        myName = name;
    }

    public String getName()
    {
        return myName;
    }

    @Deprecated
    public void init(Repository repo, Map<String, Packages> packages)
            throws PackageException
    {
        for ( Packages pp : packages.values() ) {
            for ( Package pkg : pp.packages() ) {
                init(repo, pkg);
            }
        }
    }

    /**
     * Initialize a package for a specific extension.
     * 
     * When loading a package from the repository, an extension has a chance to
     * initialize it.  For instance by parsing an extension-specific descriptor.
     * This function is not supposed to modify the state of the repository, like
     * {@code install()} does.
     */
    public abstract void init(Repository repo, Package pkg)
            throws PackageException;

    /**
     * Additional installation actions needed by a specific extension.
     * 
     * When installing a new package in the repository, an extension has a
     * chance to perform additional actions.  For instance caching additional
     * management information in extension-specific files in the repository.
     * 
     * This function is supposed to initialize the {@link Package} object as
     * well, like {@code init()} does.
     */
    public abstract void install(Repository repo, Package pkg)
            throws PackageException;

    private final String myName;
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
