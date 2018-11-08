/****************************************************************************/
/*  File:       TestConfigProcessor.java                                    */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:                                                                   */
/*  Tags:                                                                   */
/*      Copyright (c) 2009-2013 Florent Georges (see end of file.)          */
/* ------------------------------------------------------------------------ */


package transform;

import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
//import javax.xml.xquery.XQConnection;
//import javax.xml.xquery.XQDataSource;
//import javax.xml.xquery.XQException;
//import javax.xml.xquery.XQPreparedExpression;
//import javax.xml.xquery.XQResultSequence;
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
//import net.sf.saxon.xqj.SaxonXQDataSource;
import org.expath.pkg.repo.FileSystemStorage;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.Storage;
import org.expath.pkg.saxon.ConfigHelper;
import org.expath.pkg.saxon.SaxonRepository;
import org.junit.Test;

/**
 * ...
 *
 * @author Florent Georges
 */
public class TestConfigProcessor
{
    public TestConfigProcessor()
            throws PackageException
    {
        Storage storage = new FileSystemStorage(new File("test/java/transform/repo").toPath());
        REPO = new SaxonRepository(storage);
    }

    @Test(
        // error while compiling the stylesheet...
        expected=SaxonApiException.class
    )
    public void configProcessor_fail()
            throws TransformerException
                 , SaxonApiException
                 , IOException
    {
        System.err.println("configProcessor_fail");
        Processor proc = new Processor(false);

        XsltCompiler compiler = proc.newXsltCompiler();
        Source style = new StreamSource(STYLE_STD);
        XsltExecutable exec = compiler.compile(style);

        XsltTransformer trans = exec.load();
        trans.setInitialTemplate(new QName("main"));
        Serializer serial = new Serializer();
        serial.setOutputStream(OUT);
        trans.setDestination(serial);
        trans.transform();
        OUT.flush();
    }

    @Test
    public void configProcessor_successful()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , XPathException
                 , PackageException
    {
        System.err.println("configProcessor_successful");
        Processor proc = new Processor(false);
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(proc.getUnderlyingConfiguration());

        XsltCompiler compiler = proc.newXsltCompiler();
        Source style = new StreamSource(STYLE_STD);
        XsltExecutable exec = compiler.compile(style);

        XsltTransformer trans = exec.load();
        trans.setInitialTemplate(new QName("main"));
        Serializer serial = new Serializer();
        serial.setOutputStream(OUT);
        trans.setDestination(serial);
        trans.transform();
        OUT.flush();
    }

    @Test(
        // error while compiling the stylesheet...
        expected=TransformerConfigurationException.class
    )
    public void configFactory_fail()
            throws TransformerException
                 , SaxonApiException
                 , IOException
    {
        System.err.println("configFactory_fail");
        TransformerFactoryImpl factory = new TransformerFactoryImpl();
        Source style = new StreamSource(STYLE_STD);
        Transformer trans = factory.newTransformer(style);
        Result res = new StreamResult(OUT);
        trans.transform(style, res);
        OUT.flush();
    }

    @Test
    public void configFactory_successful()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , XPathException
                 , PackageException
    {
        System.err.println("configFactory_successful");
        TransformerFactoryImpl factory = new TransformerFactoryImpl();
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(factory.getConfiguration());

        Source style = new StreamSource(STYLE_STD);
        Transformer trans = factory.newTransformer(style);
        Result res = new StreamResult(OUT);
        trans.transform(style, res);
        OUT.flush();
    }

    @Test(
        // error while compiling the stylesheet...
        expected=TransformerConfigurationException.class
    )
    public void configConfiguration_fail()
            throws TransformerException
                 , SaxonApiException
                 , IOException
    {
        System.err.println("configConfiguration_fail");
        Configuration config = new Configuration();
        TransformerFactoryImpl factory = new TransformerFactoryImpl(config);
        Source style = new StreamSource(STYLE_STD);
        Transformer trans = factory.newTransformer(style);
        Result res = new StreamResult(OUT);
        trans.transform(style, res);
        OUT.flush();
    }

    @Test
    public void configConfiguration_successful()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , XPathException
                 , PackageException
    {
        System.err.println("configConfiguration_successful");
        Configuration config = new Configuration();
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);

        TransformerFactoryImpl factory = new TransformerFactoryImpl(config);
        Source style = new StreamSource(STYLE_STD);
        Transformer trans = factory.newTransformer(style);
        Result res = new StreamResult(OUT);
        trans.transform(style, res);
        OUT.flush();
    }

    @Test(
        // error while compiling the stylesheet...
        expected=TransformerConfigurationException.class
    )
    public void usingJava_fail()
            throws TransformerException
                 , SaxonApiException
                 , IOException
    {
        System.err.println("usingJava_fail");
        Configuration config = new Configuration();
        TransformerFactoryImpl factory = new TransformerFactoryImpl(config);
        Source style = new StreamSource(STYLE_JAVA);
        Transformer trans = factory.newTransformer(style);
        Result res = new StreamResult(OUT);
        trans.transform(style, res);
        OUT.flush();
    }

    @Test
    public void usingJava_successful()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , XPathException
                 , PackageException
    {
        System.err.println("usingJava_successful");
        Configuration config = new Configuration();
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);

        TransformerFactoryImpl factory = new TransformerFactoryImpl(config);
        Source style = new StreamSource(STYLE_JAVA);
        Transformer trans = factory.newTransformer(style);
        Result res = new StreamResult(OUT);
        trans.transform(style, res);
        OUT.flush();
    }

    @Test(
        // error while compiling the stylesheet...
        expected=SaxonApiException.class
    )
    public void queryProcessor_fail()
            throws TransformerException
                 , SaxonApiException
                 , IOException
    {
        System.err.println("queryProcessor_fail");
        Processor proc = new Processor(false);
        XQueryCompiler compiler = proc.newXQueryCompiler();
        XQueryExecutable exec = compiler.compile(new File(QUERY_STD));

        XQueryEvaluator eval = exec.load();
        Serializer serial = new Serializer();
        serial.setOutputStream(OUT);
        eval.evaluate();
        OUT.flush();
    }

    @Test
    public void queryProcessor_successful()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , XPathException
                 , PackageException
    {
        System.err.println("queryProcessor_successful");
        Processor proc = new Processor(false);
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(proc.getUnderlyingConfiguration());

        XQueryCompiler compiler = proc.newXQueryCompiler();
        XQueryExecutable exec = compiler.compile(new File(QUERY_STD));

        XQueryEvaluator eval = exec.load();
        Serializer serial = new Serializer();
        serial.setOutputStream(OUT);
        eval.evaluate();
        OUT.flush();
    }

    @Test(
        // error while compiling the stylesheet...
        expected=XPathException.class
    )
    public void queryConfiguration_fail()
            throws TransformerException
                 , SaxonApiException
                 , IOException
    {
        System.err.println("queryConfiguration_fail");
        Configuration config = new Configuration();
        //StaticQueryContext static_ctxt = config.newStaticQueryContext();
        StaticQueryContext static_ctxt = new StaticQueryContext(config);
        Reader query = new FileReader(new File(QUERY_STD));
        XQueryExpression expr = static_ctxt.compileQuery(query);
        DynamicQueryContext dyn_ctxt = new DynamicQueryContext(config);
        expr.run(dyn_ctxt, new StreamResult(OUT), null);
        OUT.flush();
    }

    @Test
    public void queryConfiguration_successful()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , XPathException
                 , PackageException
    {
        System.err.println("queryConfiguration_successful");
        Configuration config = new Configuration();
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);

        //StaticQueryContext static_ctxt = config.newStaticQueryContext();
        StaticQueryContext static_ctxt = new StaticQueryContext(config);
        Reader query = new FileReader(new File(QUERY_STD));
        XQueryExpression expr = static_ctxt.compileQuery(query);
        DynamicQueryContext dyn_ctxt = new DynamicQueryContext(config);
        expr.run(dyn_ctxt, new StreamResult(OUT), null);
        OUT.flush();
    }

    @Test(
        // error while compiling the stylesheet...
        expected=XPathException.class
    )
    public void queryUsingJava_fail()
            throws TransformerException
                 , SaxonApiException
                 , IOException
    {
        System.err.println("queryUsingJava_fail");
        Configuration config = new Configuration();
        //StaticQueryContext static_ctxt = config.newStaticQueryContext();
        StaticQueryContext static_ctxt = new StaticQueryContext(config);
        Reader query = new FileReader(new File(QUERY_JAVA));
        XQueryExpression expr = static_ctxt.compileQuery(query);
        DynamicQueryContext dyn_ctxt = new DynamicQueryContext(config);
        expr.run(dyn_ctxt, new StreamResult(OUT), null);
        OUT.flush();
    }

    @Test
    public void queryUsingJava_successful()
            throws TransformerException
                 , SaxonApiException
                 , IOException
                 , XPathException
                 , PackageException
    {
        System.err.println("queryUsingJava_successful");
        Configuration config = new Configuration();
        ConfigHelper helper = new ConfigHelper(REPO);
        helper.config(config);

        //StaticQueryContext static_ctxt = config.newStaticQueryContext();
        StaticQueryContext static_ctxt = new StaticQueryContext(config);
        Reader query = new FileReader(new File(QUERY_JAVA));
        XQueryExpression expr = static_ctxt.compileQuery(query);
        DynamicQueryContext dyn_ctxt = new DynamicQueryContext(config);
        expr.run(dyn_ctxt, new StreamResult(OUT), null);
        OUT.flush();
    }

//    @Test(
//        // error while compiling the stylesheet...
//        expected=XQException.class
//    )
//    public void queryXQJ_fail()
//            throws XQException
//                 , FileNotFoundException
//    {
//        System.err.println("queryXQJ_fail");
//        XQDataSource ds = new SaxonXQDataSource();
//        XQConnection conn = ds.getConnection();
//        Reader query = new FileReader(new File(QUERY_STD));
//        XQPreparedExpression expr = conn.prepareExpression(query);
//        XQResultSequence result = expr.executeQuery();
//        while ( result.next() ) {
//            // System.out.println(result.getItemAsString(null));
//        }
//        System.out.flush();
//    }
//
//    @Test
//    public void queryXQJ_successful()
//            throws XQException
//                 , FileNotFoundException
//                 , XPathException
//                 , PackageException
//    {
//        System.err.println("queryXQJ_successful");
//        Configuration config = new Configuration();
//        ConfigHelper helper = new ConfigHelper(REPO);
//        helper.config(config);
//
//        XQDataSource ds = new SaxonXQDataSource(config);
//        XQConnection conn = ds.getConnection();
//        Reader query = new FileReader(new File(QUERY_STD));
//        XQPreparedExpression expr = conn.prepareExpression(query);
//        XQResultSequence result = expr.executeQuery();
//        while ( result.next() ) {
//            // System.out.println(result.getItemAsString(null));
//        }
//        System.out.flush();
//    }

    private SaxonRepository REPO;
    private static final String STYLE_STD  = "test/java/transform/style.xsl";
    private static final String STYLE_JAVA = "test/java/transform/using-java.xsl";
    private static final String QUERY_STD  = "test/java/transform/query.xq";
    private static final String QUERY_JAVA = "test/java/transform/using-java.xq";
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
