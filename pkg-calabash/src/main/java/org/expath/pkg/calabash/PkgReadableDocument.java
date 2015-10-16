/****************************************************************************/
/*  File:       PkgReadableDocument.java                                    */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-10-20                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.calabash;

import com.xmlcalabash.core.XProcException;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.io.DocumentSequence;
import com.xmlcalabash.io.ReadablePipe;
import com.xmlcalabash.model.Step;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

/**
 * ...
 *
 * Based on ReadableDocument (Calabash 0.9.15, SVN revision 456.)
 *
 * @author Florent Georges
 */
public class PkgReadableDocument
        implements ReadablePipe
{
    public PkgReadableDocument(String href, URIResolver resolver, XProcRuntime runtime)
    {
        myHref     = href;
        myResolver = resolver;
        myRuntime  = runtime;
        myDocs     = new DocumentSequence(myRuntime);
    }

    @Override
    public void canReadSequence(boolean sequence)
    {
        // nop; always false
    }

    @Override
    public void resetReader()
    {
        myPos = 0;
    }

    @Override
    public void setReader(Step step)
    {
        // nothing
    }

    @Override
    public boolean moreDocuments()
    {
        if ( ! myHasBeenRead ) {
            doRead();
        }
        return myPos < myDocs.size();
    }

    @Override
    public boolean closed()
    {
        return true;
    }

    @Override
    public int documentCount()
    {
        return myDocs.size();
    }

    @Override
    public DocumentSequence documents()
    {
        return myDocs;
    }

    @Override
    public XdmNode read()
            throws SaxonApiException
    {
        if ( ! myHasBeenRead ) {
            doRead();
        }
        return myDocs.get(myPos++);
    }

    @Override
    public boolean readSequence()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setNames(String step, String port)
    {
        // nop
    }

    private void doRead()
    {
        Source src;
        try {
            src = myResolver.resolve(myHref, null);
        }
        catch ( TransformerException ex ) {
            throw new XProcException("Error resolving '" + myHref + "'", ex);
        }
        DocumentBuilder builder = myRuntime.getProcessor().newDocumentBuilder();
        builder.setLineNumbering(true);
        try {
            myDocs.add(builder.build(src));
            myHasBeenRead = true;
        }
        catch ( SaxonApiException ex ) {
            String msg = "Error building '" + myHref + "' (" + src.getSystemId() + ")";
            throw new XProcException(msg, ex);
        }
    }

    private String myHref;
    private URIResolver myResolver;
    private XProcRuntime myRuntime;
    private int myPos = 0;
    private DocumentSequence myDocs = null;
    private boolean myHasBeenRead = false;
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
