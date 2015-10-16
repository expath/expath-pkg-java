/****************************************************************************/
/*  File:       EXPathConfiguration.java                                    */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-07-28                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package scrapbook.catalog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import net.sf.saxon.trans.XPathException;
import org.expath.pkg.repo.FileSystemStorage;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.Storage;
import org.expath.pkg.saxon.ConfigHelper;
import org.expath.pkg.saxon.SaxonRepository;
import org.junit.Test;

/**
 * Test the repo configuration of the several Saxon invocation mechanisms.
 * 
 * @author Florent Georges
 */
public class ConfigHelperTest
{
    public ConfigHelperTest()
            throws PackageException
    {
        Storage storage = new FileSystemStorage(new File("test/java/transform/repo"));
        REPO = new SaxonRepository(storage);
    }

    // Using S9api Processor.
    @Test
    public void xslt_s9api()
            throws TransformerException
                 , SaxonApiException
                 , PackageException
    {
        // the config object
        Configuration config = new Configuration();
        // configure the config object for Packaging System
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);
        // the processor
        Processor proc = new Processor(config);
        // compiling
        XsltCompiler compiler = proc.newXsltCompiler();
        Source style = new StreamSource(getResource(XSLT_NAME));
        XsltExecutable exec = compiler.compile(style);
        // actually evaluate
        XsltTransformer trans = exec.load();
        trans.setInitialTemplate(new QName("main"));
        Serializer out = new Serializer(OUT);
        trans.setDestination(out);
        trans.transform();
    }

    // Using S9api Processor.
    @Test
    public void xquery_s9api()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , PackageException
    {
        // the config object
        Configuration config = new Configuration();
        // configure the config object for Packaging System
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);
        // the processor
        Processor proc = new Processor(config);
        // compiling
        XQueryCompiler compiler = proc.newXQueryCompiler();
        XQueryExecutable exec = compiler.compile(getResource(XQUERY_NAME));
        // actually evaluate
        XQueryEvaluator eval = exec.load();
        Serializer out = new Serializer(OUT);
        eval.run(out);
    }

    // Using JAXP factory.
    @Test
    public void xslt_jaxp()
            throws TransformerException
                 , PackageException
    {
        // the factory
        TransformerFactoryImpl factory = new TransformerFactoryImpl();
        // configure the factory for Packaging System
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(factory.getConfiguration());
        // compiling
        Source style = new StreamSource(getResource(XSLT_NAME));
        Templates templates = factory.newTemplates(style);
        // actually transform
        Transformer trans = templates.newTransformer();
        Source src = new StreamSource(getResource(XSLT_NAME));
        Result res = new StreamResult(OUT);
        trans.transform(src, res);
    }

    // Using Saxon Configuration.
    @Test
    public void xquery_legacy()
            throws TransformerException
                 , XPathException
                 , IOException
                 , PackageException
    {
        // the config object
        Configuration config = new Configuration();
        // configure the config object for Packaging System
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);
        // compiling
        StaticQueryContext ctxt = config.newStaticQueryContext();
        XQueryExpression exp = ctxt.compileQuery(getResource(XQUERY_NAME), "utf-8");
        // actually evaluate
        DynamicQueryContext dyn = new DynamicQueryContext(config);
        Result res = new StreamResult(OUT);
        exp.run(dyn, res, null);
    }

    private InputStream getResource(String resource)
    {
        ClassLoader loader = getClass().getClassLoader();
        return loader.getResourceAsStream(resource);
    }

    private final SaxonRepository REPO;
//    private static final String XSLT_NAME = "scrapbook/catalog/http-test.xsl";
//    private static final String XQUERY_NAME = "scrapbook/catalog/http-test.xq";
    private static final String XSLT_NAME = "transform/style.xsl";
    private static final String XQUERY_NAME = "transform/query.xq";
    // private static final OutputStream OUT  = System.err;
    private static final OutputStream OUT  = new DevNull();

    private static class DevNull
            extends OutputStream
    {
        @Override
        public void close() throws IOException {
            // nothing
        }
        @Override
        public void flush() throws IOException {
            // nothing
        }
        @Override
        public void write(byte[] b) throws IOException {
            // nothing
        }
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            // nothing
        }
        @Override
        public void write(int b) throws IOException {
            // nothing
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
