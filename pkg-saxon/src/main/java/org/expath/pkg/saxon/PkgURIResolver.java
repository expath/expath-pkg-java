/****************************************************************************/
/*  File:       PkgURIResolver.java                                         */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2010-05-02                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010-2015 Florent Georges (see end of file.)          */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.saxon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.URISpace;
import org.expath.pkg.repo.tools.Logger;

/**
 * URI Resolver to resolve within a repository.
 *
 * @author Florent Georges
 */
public class PkgURIResolver
        implements URIResolver
{
    public PkgURIResolver(Map<String, String> overrides, SaxonRepository repo, URIResolver parent, URISpace space)
            throws PackageException
    {
        myOverrides = overrides;
        myRepo = repo;
        myParent = parent;
        mySpace = space;
    }

    @Override
    public Source resolve(String href, String base)
            throws TransformerException
    {
        LOG.fine("resolve: {0} with base: {1}", href, base);
        // try the override URIs
        String override = myOverrides.get(href);
        if ( override != null ) {
            File f = new File(override);
            StreamSource s;
            try {
                s = new StreamSource(new FileInputStream(f));
            }
            catch ( FileNotFoundException ex ) {
                throw new TransformerException("Error resolving the URI", ex);
            }
            s.setSystemId(f.toURI().toString());
            return s;
        }
        // try a Saxon-specific stuff
        try {
            Source s = myRepo.resolve(href, mySpace);
            if ( s != null ) {
                return s;
            }
        }
        catch ( PackageException ex ) {
            throw new TransformerException("Error resolving the URI", ex);
        }
        // delegate to pkg-repo's resolver
        return myParent.resolve(href, base);
    }

    /** The overrides (take precedence over the catalog resolver). */
    private final Map<String, String> myOverrides;
    /** The Saxon repo used to resolve Saxon-specific stuff. */
    private final SaxonRepository myRepo;
    /** The parent resolver, from pkg-repo. */
    private final URIResolver myParent;
    /** ... */
    private final URISpace mySpace;
    /** The logger. */
    private static final Logger LOG = Logger.getLogger(PkgURIResolver.class);
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
