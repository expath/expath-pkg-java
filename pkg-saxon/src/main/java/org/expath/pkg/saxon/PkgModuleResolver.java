/****************************************************************************/
/*  File:       PkgModuleResolver.java                                      */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-10-29                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009-2015 Florent Georges (see end of file.)          */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.saxon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.lib.ModuleURIResolver;
import net.sf.saxon.trans.XPathException;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.Repository;
import org.expath.pkg.repo.URISpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for EXPath Pkg of Saxon's {@link ModuleURIResolver} for XQuery.
 *
 * @author Florent Georges
 */
public class PkgModuleResolver
        implements ModuleURIResolver
{
    public PkgModuleResolver(Map<String, String> overrides, SaxonRepository repo, Repository parent)
            throws PackageException
    {
        myOverrides = overrides;
        myRepo = repo;
        myParent = parent;
    }

    @Override
    public StreamSource[] resolve(String module_uri, String base_uri, String[] locations)
            throws XPathException
    {
        LOG.debug("resolve: {} with base: {}", module_uri, base_uri);
        for ( String l : locations ) {
            LOG.debug("  location: {}", l);
        }

        try {
            // try the override URIs
            String href = myOverrides.get(module_uri);
            if ( href != null ) {
                File f = new File(href);
                InputStream in;
                try {
                    in = new FileInputStream(f);
                }
                catch ( FileNotFoundException ex ) {
                    throw new PackageException("Error opening file", ex);
                }
                StreamSource[] source = new StreamSource[1];
                source[0] = new StreamSource(in);
                source[0].setSystemId(f.toURI().toString());
                return source;
            }
            // try a Saxon-specific stuff
            Source s = myRepo.resolve(module_uri, URISpace.XQUERY);
            if ( s != null ) {
                // TODO: Why requiring a StreamSource here...?
                if ( ! (s instanceof StreamSource) ) {
                    throw new XPathException("The Source is not a StreamSource");
                }
                return new StreamSource[]{ (StreamSource) s };
            }
            // delegate to pkg-repo's repository
            s = myParent.resolve(module_uri, URISpace.XQUERY);
            if ( s != null ) {
                // TODO: Needs a better abstraction.  Even if it is not a StreamSource
                // it could succeed...  But a Source is not suitable for an XQuery
                // module source.
                if ( ! (s instanceof StreamSource) ) {
                    throw new XPathException("The Source is not a StreamSource");
                }
                return new StreamSource[]{ (StreamSource) s };
            }
        }
        catch ( PackageException ex ) {
            throw new XPathException("Error resolving the URI", ex);
        }
        return null;
    }

    /** The overrides (take precedence over the catalog resolver). */
    private final Map<String, String> myOverrides;
    /** The Saxon repo used to resolve Saxon-specific stuff. */
    private final SaxonRepository myRepo;
    /** The pkg-repo's repository, to delegate everything else to. */
    private final Repository myParent;
    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger(PkgModuleResolver.class);
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
