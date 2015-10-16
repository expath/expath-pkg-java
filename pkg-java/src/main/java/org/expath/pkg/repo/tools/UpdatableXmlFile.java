/****************************************************************************/
/*  File:       UpdatableXmlFile.java                                       */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2013-09-17                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2013 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo.tools;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.expath.pkg.repo.PackageException;

/**
 * An XML file, that can be updated with XSLT stylesheets.
 *
 * @author Florent Georges
 */
public abstract class UpdatableXmlFile
        extends UpdatableFile
{
    /**
     * Create a new instance.
     */
    public UpdatableXmlFile(Path file)
            throws PackageException
    {
        super(file);
    }

    /**
     * Compile a stylesheet.
     */
    protected Transformer compile(String rsrc_name)
            throws PackageException
    {
        try {
            // cache the compiled stylesheet?
            ClassLoader loader = UpdatableXmlFile.class.getClassLoader();
            InputStream style_in = loader.getResourceAsStream(rsrc_name);
            if ( style_in == null ) {
                throw new PackageException("Resource not found: " + rsrc_name);
            }
            Source style_src = new StreamSource(style_in);
            style_src.setSystemId(rsrc_name);
            Templates style = TransformerFactory.newInstance().newTemplates(style_src);
            return style.newTransformer();
        }
        catch ( TransformerConfigurationException ex ) {
            throw new PackageException("Impossible to compile the stylesheet: " + rsrc_name, ex);
        }
    }

    /**
     * Transform the file with the transformer.
     */
    protected void transform(Transformer trans)
            throws PackageException
    {
        try {
            Source src = new StreamSource(myFile.toFile());
            StringWriter res_out = new StringWriter();
            Result res = new StreamResult(res_out);
            trans.transform(src, res);
            update(res_out);
        }
        catch ( TransformerException ex ) {
            throw new PackageException("Error transforming the file: " + myFile, ex);
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
