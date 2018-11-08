/****************************************************************************/
/*  File:       DescriptorParserTest.java                                   */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2010-12-02                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010-2013 Florent Georges (see end of file.)          */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo.parser;

import java.net.URISyntaxException;
import javax.xml.transform.Source;
import java.net.URI;
import org.expath.pkg.repo.Storage.PackageResolver;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.transform.stream.StreamSource;
import org.expath.pkg.repo.FileSystemStorage;
import org.expath.pkg.repo.Package;
import org.expath.pkg.repo.Storage;
import org.expath.pkg.repo.URISpace;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * TODO: ...
 *
 * @author Florent Georges
 */
public class DescriptorParserTest
{
    @Test
    public void testParse()
            throws Exception
    {
        // the SUT
        DescriptorParser sut = new DescriptorParser();
        Source desc = new StreamSource(PKG_DIR + "expath-pkg.xml");
        Storage storage = new FileSystemStorage(Paths.get("target/test-classes/repos/simple"));
        // get the package
        Package pkg = sut.parse(desc, "hello-1.1.1", storage, null);
        // the simple properties
        assertEquals("the abbrev", "hello", pkg.getAbbrev());
        assertEquals("the name", "http://www.example.org/lib/hello", pkg.getName());
        assertEquals("the version", "1.1.1", pkg.getVersion());
        // the resolver
        PackageResolver resolver = pkg.getResolver();
        assertEquals("the resource (dir) name", "hello-1.1.1", resolver.getResourceName());
        assertSourceIsFile("the resource",                "expath-pkg.xml",  resolver.resolveResource("expath-pkg.xml"));
        assertSourceIsFile("the component as a resource", "hello/hello.xq",  resolver.resolveResource("hello/hello.xq"));
        assertSourceIsFile("the component",               "hello/hello.xsl", resolver.resolveComponent("hello.xsl"));
        // the resolve method with correct public URIs
        assertSourceIsFile("resolve XQuery OK",           "hello/hello.xq",  pkg.resolve("http://www.example.org/hello", URISpace.XQUERY));
        assertSourceIsFile("resolve XSLT OK",             "hello/hello.xsl", pkg.resolve("http://www.example.org/hello.xsl", URISpace.XSLT));
        // the resolve method with wrong public URIs
        assertNull("resolve non existing", pkg.resolve("http://www.example.org/dummy.xsl", URISpace.XSLT));
        assertNull("resolve XQuery within XSLT", pkg.resolve("http://www.example.org/hello", URISpace.XSLT));
        assertNull("resolve XSLT within XQuery", pkg.resolve("http://www.example.org/hello.xsl", URISpace.XQUERY));
    }

    static private void assertSourceIsFile(String msg, String relative_file, Source src)
            throws URISyntaxException
    {
        Path f = Paths.get(PKG_DIR + relative_file);
        URI uri = new URI(src.getSystemId());
        assertEquals(msg, f.toAbsolutePath(), Paths.get(uri).toAbsolutePath());
    }

    static private final String PKG_DIR = "target/test-classes/repos/simple/hello-1.1.1/";
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
