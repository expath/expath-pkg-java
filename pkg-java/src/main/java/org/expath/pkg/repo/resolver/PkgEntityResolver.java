/****************************************************************************/
/*  File:       PkgEntityResolver.java                                      */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2010-05-15                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo.resolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.URISpace;
import org.expath.pkg.repo.Universe;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Implementation of {@link EntityResolver} based on a repository and a URI space.
 *
 * @author Florent Georges
 */
public class PkgEntityResolver
        implements EntityResolver
{
    public PkgEntityResolver(Universe universe, URISpace space)
    {
        myUniverse = universe;
        mySpace    = space;
    }

    // TODO: What to do with the public ID?  Ignore it, check it is null?
    // Try to resolve it as well?  I would say ignore, as for me the packaging
    // system is based more on system IDs...
    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException
                 , IOException
    {
        // try to resolve the system ID as a file in the repo
        Source resolved;
        try {
            resolved = myUniverse.resolve(systemId, mySpace);
        }
        catch ( PackageException ex ) {
            // TODO: ...
            System.err.println("TODO: Error management. Something wrong in entity resolver...");
            ex.printStackTrace();
            return null;
        }
        // use it if it is there
        if ( resolved == null ) {
            return null;
        }
        InputSource src = sourceToInputSource(resolved);
        src.setSystemId(resolved.getSystemId());
        return src;
    }

    /**
     * Return the InputSource from a Source.
     * 
     * From http://www.java2s.com/Code/Java/XML/SourceToInputSource.htm.
     */
    private InputSource sourceToInputSource(Source src)
            throws IOException
    {
        if (src instanceof SAXSource ) {
            SAXSource sax = (SAXSource) src;
            return sax.getInputSource();
        }
        else if ( src instanceof DOMSource ) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DOMSource dom = (DOMSource) src;
            Node node = dom.getNode();
            if ( node instanceof Document ) {
                Document doc = (Document) node;
                node = doc.getDocumentElement();
            }
            Element root = (Element) node;
            serialize(root, buffer);
            InputSource result = new InputSource(src.getSystemId());
            result.setByteStream(new ByteArrayInputStream(buffer.toByteArray()));
            return result;
        }
        else if ( src instanceof StreamSource ) {
            StreamSource ss = (StreamSource) src;
            InputSource result = new InputSource(ss.getSystemId());
            result.setByteStream(ss.getInputStream());
            result.setCharacterStream(ss.getReader());
            result.setPublicId(ss.getPublicId());
            return result;
        }
        else {
            return new InputSource(src.getSystemId());
        }
    }

    private void serialize(Element element, OutputStream out)
            throws IOException
    {
        try {
            DOMSource source = new DOMSource(element);
            StreamResult result = new StreamResult(out);
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            transformer.transform(source, result);
        }
        catch ( TransformerException ex ) {
            throw new IOException("Error serializing in memory...", ex);
        }
    }

    private Universe myUniverse;
    private URISpace mySpace;
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
