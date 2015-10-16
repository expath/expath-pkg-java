/****************************************************************************/
/*  File:       PkgReadableData.java                                        */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-10-21                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.calabash;

import com.xmlcalabash.core.XProcConstants;
import com.xmlcalabash.core.XProcException;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.io.DocumentSequence;
import com.xmlcalabash.io.ReadableData;
import com.xmlcalabash.io.ReadablePipe;
import com.xmlcalabash.model.Step;
import com.xmlcalabash.util.Base64;
import com.xmlcalabash.util.TreeWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * ...
 *
 * Based on ReadableData (Calabash 0.9.15, SVN revision 456.)
 *
 * @author Florent Georges
 */
public class PkgReadableData
        implements ReadablePipe
{
    // TODO: Why do not use the same approach as in ReadableDocument (and
    // PkgReableDocument) which uses lazy loading.  Why loading documents in
    // ctor (as in ReadableData)?
    //
    // TODO: Why not pass the DataBinding object, instead of href, wrapper,
    // content type, etc.?
    public PkgReadableData(String href, QName wrapper, String content_type, EntityResolver resolver, XProcRuntime runtime)
    {
        myHref     = href;
        myWrapper  = wrapper;
        myConType  = content_type;
        myResolver = resolver;
        myRuntime  = runtime;
        myDocs     = new DocumentSequence(myRuntime);

        // TODO: Localize more precisely regarding their uses.
        String user_con_type = parseContentType(myConType);
        String user_charset  = parseCharset(myConType);

        if ( myHref == null ) {
            return;
        }

        InputSource src;
        try {
            src = myResolver.resolveEntity(null, myHref);
            if ( src == null ) {
                // TODO: What to do if not resolved?  Delegate to ReadableData?
                //
                // Well, if the user set pkg:kind, then we can say if it is not
                // in the repository, then this is an error...
                throw new XProcException("Data not found (" + myHref + ")");
            }
        }
        catch ( SAXException ex ) {
            throw new XProcException("Error resolving the URI (" + myHref + ")", ex);
        }
        catch ( IOException ex ) {
            throw new XProcException("Error resolving the URI (" + myHref + ")", ex);
        }

        URI uri;
        try {
            uri = new URI(src.getSystemId());
        }
        catch ( URISyntaxException ex ) {
            throw new XProcException("Not a proper URI (" + myHref + ")", ex);
        }

        TreeWriter tree = new TreeWriter(runtime);
        tree.startDocument(uri);

        try {
            InputStream stream = src.getByteStream();
            // TODO: How to get the server Content-Type when using an
            // InputSource?
            // String server_con_type = connection.getContentType();
            String server_con_type = myConType == null ? "text/plain" : myConType;

            if ( "content/unknown".equals(server_con_type) && myConType != null ) {
                // pretend...
                server_con_type = myConType;
            }

            String server_base_con_type = parseContentType(server_con_type);
            String server_charset = parseCharset(server_con_type);

            /* FIXME:
            // HACK! HACK! HACK! Just so that the test cases work in the test suite
            serverContentType = ctype + ";charset=" + charset.toUpperCase() + post;
            */

            // If the user specified a charset and the server did not and it's a file: URI,
            // assume the user knows best.
            // FIXME: provide some way to override this!!!

            String charset = server_charset;
            // if ("file".equals(url.getProtocol())
            //         && server_charset == null
            //         && server_base_con_type.equals(user_con_type)) {
            if ( server_charset == null
                    && server_base_con_type.equals(user_con_type) ) {
                charset = user_charset;
            }

            tree.addStartElement(wrapper);
            if ( XProcConstants.c_data.equals(wrapper) ) {
                if ( "content/unknown".equals(server_con_type) ) {
                    tree.addAttribute(ReadableData._contentType, "application/octet-stream");
                }
                else {
                    tree.addAttribute(ReadableData._contentType, server_con_type);
                }
                if ( ! isText(server_con_type, charset) ) {
                    tree.addAttribute(ReadableData._encoding, "base64");
                }
            }
            else {
                if ( "content/unknown".equals(server_con_type) ) {
                    tree.addAttribute(ReadableData.c_contentType, "application/octet-stream");
                }
                else {
                    tree.addAttribute(ReadableData.c_contentType, server_con_type);
                }
                if ( ! isText(server_con_type, charset) ) {
                    tree.addAttribute(ReadableData.c_encoding, "base64");
                }
            }
            tree.startContent();


            if ( isText(server_con_type, charset) ) {
                if ( charset == null ) {
                    // FIXME: Is this right? I think it is...
                    charset = "UTF-8";
                }
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(stream, charset));
                int buflen = 4096 * 3;
                char[] chars = new char[buflen];
                int read = bufreader.read(chars, 0, buflen);
                while ( read >= 0 ) {
                    if ( read > 0 ) {
                        String data = new String(chars, 0, read);
                        tree.addText(data);
                    }
                    read = bufreader.read(chars, 0, buflen);
                }
                bufreader.close();
            }
            else {
                // Fill the buffer each time so that we get an even number of base64 lines
                int buflen = 4096 * 3;
                byte[] bytes = new byte[buflen];
                int pos = 0;
                int readlen = buflen;
                boolean done = false;
                while ( ! done ) {
                    int read = stream.read(bytes, pos, readlen);
                    if ( read >= 0 ) {
                        pos += read;
                        readlen -= read;
                    }
                    else {
                        done = true;
                    }
                    if ( (readlen == 0) || done ) {
                        String base64 = Base64.encodeBytes(bytes, 0, pos);
                        tree.addText(base64 + "\n");
                        pos = 0;
                        readlen = buflen;
                    }
                }
                stream.close();
            }
        }
        catch ( IOException ex ) {
            throw new XProcException("Error reading the data content", ex);
        }

        tree.addEndElement();
        tree.endDocument();

        XdmNode doc = tree.getResult();
        myDocs.add(doc);
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

    // TODO: Copied from ReadableData, adapt following RFC 3023.  See also what
    // I've done in EXPath HTTP Client for Saxon.
    private boolean isText(String content_type, String charset)
    {
        return ("application/xml".equals(content_type)
                || content_type.endsWith("+xml")
                || content_type.startsWith("text/")
                || "utf-8".equals(charset));
    }

    // TODO: Same comments as for isText()...
    private String parseContentType(String content_type)
    {
        if ( content_type == null ) {
            return null;
        }
        int pos = content_type.indexOf(";");
        if ( pos > 0 ) {
            String type = content_type.substring(0, pos).trim();
            return type;
        } else {
            return content_type;
        }
    }

    // TODO: Same comments as for parseContentType()...
    private String parseCharset(String content_type)
    {
        if (content_type == null) {
            return null;
        }
        int pos = content_type.indexOf(";");
        if ( pos > 0 ) {
            String charset = content_type.substring(pos);
            charset = charset.replaceAll(";\\s+", ";").replaceAll("\\s+;", ";");
            if ( charset.contains(";charset=") ) {
                pos     = charset.indexOf(";charset=");
                charset = charset.substring(pos + 9);
                pos     = charset.indexOf(";");
                if ( pos >= 0 ) {
                    charset = charset.substring(0, pos);
                }
                return charset.toLowerCase();
            }
        }
        return null;
    }

    private String myHref;
    private QName myWrapper;
    private String myConType;
    private EntityResolver myResolver;
    private XProcRuntime myRuntime;
    private int myPos = 0;
    private DocumentSequence myDocs = null;
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
