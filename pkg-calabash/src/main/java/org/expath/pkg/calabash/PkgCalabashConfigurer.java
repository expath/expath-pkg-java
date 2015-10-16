/****************************************************************************/
/*  File:       PkgCalabashConfigurer.java                                  */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-10-19                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.calabash;

import com.xmlcalabash.core.XProcException;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.io.ReadablePipe;
import com.xmlcalabash.library.Load;
import com.xmlcalabash.model.DataBinding;
import com.xmlcalabash.model.DocumentBinding;
import com.xmlcalabash.util.DefaultXMLCalabashConfigurer;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.expath.pkg.repo.Package;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.Packages;
import org.expath.pkg.repo.resolver.PkgEntityResolver;
import org.expath.pkg.repo.Repository;
import org.expath.pkg.repo.URISpace;
import org.expath.pkg.repo.resolver.PkgURIResolver;
import org.xml.sax.EntityResolver;

/**
 * ...
 *
 * @author Florent Georges
 */
public class PkgCalabashConfigurer
        extends DefaultXMLCalabashConfigurer
{
    public PkgCalabashConfigurer(XProcRuntime runtime, Repository repo)
    {
        super(runtime);
        myRepo = repo;
    }

    // TODO: The URI resolver set on the runtime object is gonna be used for
    // other purposes as well.  Try to convince Norman there should be one
    // resolver dedicated at resolving XProc pipelines.
    @Override
    public void configRuntime(XProcRuntime runtime)
    {
        // set the URI resolver
        URIResolver resolver = new PkgURIResolver(myRepo, URISpace.XPROC);
        runtime.setURIResolver(resolver);
        // register the extension steps (recorded into Calabash's info on packages)
        for ( Packages pp : myRepo.listPackages() ) {
            Package pkg = pp.latest();
            CalabashPkgInfo info = (CalabashPkgInfo) pkg.getInfo("calabash");
            if ( info != null ) {
                try {
                    info.registerExtensionSteps(runtime);
                }
                catch ( PackageException ex ) {
                    String msg = "Error registering extension steps for ";
                    throw new XProcException(msg + pkg.getName() + ", " + pkg.getVersion(), ex);
                }
            }
        }
    }

    @Override
    public XdmNode loadDocument(Load step)
    {
        String kind = step.getStep().getExtensionAttribute(KIND);
        if ( kind == null ) {
            return super.loadDocument(step);
        }
        boolean validate = step.getOption(DTD_VAL, false);
        String  href     = step.getOption(HREF).getString();
        XdmNode doc      = parse(href, validate, kind);
        if ( doc == null ) {
            return super.loadDocument(step);
        }
        return doc;
    }

    @Override
    public ReadablePipe makeReadableData(XProcRuntime r, DataBinding b)
    {
        String kind = b.getExtensionAttribute(KIND);
        if ( kind == null ) {
            return super.makeReadableData(r, b);
        }
        else {
            EntityResolver resolver = getEntityResolver(kind);
            return new PkgReadableData(b.getHref(), b.getWrapper(), b.getContentType(), resolver, r);
        }
    }

    @Override
    public ReadablePipe makeReadableDocument(XProcRuntime r, DocumentBinding b)
    {
        String kind = b.getExtensionAttribute(KIND);
        if ( kind == null ) {
            return super.makeReadableDocument(r, b);
        }
        else {
            URIResolver resolver = getURIResolver(kind);
            return new PkgReadableDocument(b.getHref(), resolver, r);
        }
    }

    private XdmNode parse(String href, boolean validate, String kind)
    {
        try {
            URIResolver resolver = getURIResolver(kind);
            Source src = resolver.resolve(href, null);
            if ( src == null ) {
                return null;
            }
            DocumentBuilder builder = runtime.getProcessor().newDocumentBuilder();
            builder.setDTDValidation(validate);
            builder.setLineNumbering(true);
            return builder.build(src);
        }
        catch ( TransformerException ex ) {
            throw new XProcException("Error resolving " + href + " (" + kind + ")", ex);
        }
        catch ( SaxonApiException ex ) {
            throw new XProcException("Error building " + href + " (" + kind + ")", ex);
        }
    }

    private URIResolver getURIResolver(String kind)
    {
        return new PkgURIResolver(myRepo, spaceFromKind(kind));
    }

    private EntityResolver getEntityResolver(String kind)
    {
        return new PkgEntityResolver(myRepo, spaceFromKind(kind));
    }

    private URISpace spaceFromKind(String kind)
    {
        return URISpace.valueOf(kind.toUpperCase());
    }

    private Repository myRepo;
    private final static QName  KIND    = new QName("pkg", "http://expath.org/ns/pkg", "kind");
    private final static QName  HREF    = new QName("href");
    private final static QName  DTD_VAL = new QName("dtd-validate");
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
