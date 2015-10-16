/****************************************************************************/
/*  File:       PkgSaxonConfigurer.java                                     */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-10-19                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009, 2010 Florent Georges (see end of file.)         */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.calabash;

import com.xmlcalabash.core.XProcException;
import com.xmlcalabash.config.SaxonConfigurer;
import net.sf.saxon.Configuration;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.Repository;
import org.expath.pkg.saxon.ConfigHelper;
import org.expath.pkg.saxon.SaxonRepository;

/**
 * ...
 *
 * @author Florent Georges
 */
public class PkgSaxonConfigurer
        implements SaxonConfigurer
{
    public PkgSaxonConfigurer(Repository repo)
            throws PackageException
    {
        myRepo = new SaxonRepository(repo);
    }

    @Override
    public void configXQuery(Configuration config)
    {
        try {
            ConfigHelper helper = new ConfigHelper(myRepo);
            helper.config(config);
        }
        catch ( PackageException ex ) {
            String msg = "Error configuring package repository on Saxon for XQuery";
            throw new XProcException(msg, ex);
        }
    }

    @Override
    public void configXSLT(Configuration config)
    {
        try {
            ConfigHelper helper = new ConfigHelper(myRepo);
            helper.config(config);
        }
        catch ( PackageException ex ) {
            String msg = "Error configuring package repository on Saxon for XSLT";
            throw new XProcException(msg, ex);
        }
    }

    @Override
    public void configXSD(Configuration config)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void configSchematron(Configuration config)
    {
        try {
            ConfigHelper helper = new ConfigHelper(myRepo);
            helper.config(config);
        }
        catch ( PackageException ex ) {
            String msg = "Error configuring package repository on Saxon for XSLT";
            throw new XProcException(msg, ex);
        }
    }

    private SaxonRepository myRepo;
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
