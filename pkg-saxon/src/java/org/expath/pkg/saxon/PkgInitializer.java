/****************************************************************************/
/*  File:       PkgInitializer.java                                         */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2011-08-24                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2011-2015 Florent Georges (see end of file.)          */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.saxon;

import java.io.File;
import net.sf.saxon.Configuration;
import net.sf.saxon.lib.Initializer;
import org.expath.pkg.repo.FileSystemStorage;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.Storage;

/**
 * Initialize Saxon with a repository.
 * 
 * The directory where to find the repository on the disk is passed through the
 * system property {@code org.expath.pkg.saxon.repo}.
 *
 * @author Florent Georges
 */
public class PkgInitializer
        implements Initializer
{
    @Override
    public void initialize(Configuration c)
    {
        String prop = System.getProperty("org.expath.pkg.saxon.repo");
        if ( prop != null ) {
            try {
                File dir = new File(prop);
                Storage storage = new FileSystemStorage(dir.toPath());
                SaxonRepository repo = new SaxonRepository(storage);
                ConfigHelper expath_helper = new ConfigHelper(repo);
                expath_helper.config(c);
            }
            catch ( PackageException ex ) {
                throw new RuntimeException("Error in the EXPath repo config", ex);
            }
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
