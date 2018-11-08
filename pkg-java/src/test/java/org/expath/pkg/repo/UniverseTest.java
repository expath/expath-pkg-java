/****************************************************************************/
/*  File:       UniverseTest.java                                           */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2011-01-29                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2011-2013 Florent Georges (see end of file.)          */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the {@code Universe}, especially its resolve mechanism.
 *
 * @author Florent Georges
 */
public class UniverseTest
{
    private Path pkgComponentFile(String repo, String lib, String version, String component)
    {
        return Paths.get("target/test-classes/repos/" + repo + "/" + lib + "-" + version + "/" + lib + "/" + component);
    }

    private Path sourceFile(Source src)
            throws URISyntaxException
    {
        assertNotNull(src);
        String sysid = src.getSystemId();
        URI uri = new URI(sysid);
        return Paths.get(uri);
    }

    private void assertComponent(String repo, String lib, String version, String component, Source src)
            throws URISyntaxException, IOException
    {
        Path expected = pkgComponentFile(repo, lib, version, component);
        Path actual   = sourceFile(src);
        String exp_path = expected.toFile().getCanonicalPath();
        String act_path = actual.toFile().getCanonicalPath();
        assertEquals(exp_path, act_path);
    }

    private Repository getRepository(String repo)
            throws PackageException
    {
        final Path repo_dir = Paths.get("target/test-classes/repos/" + repo );
        if (Files.exists(repo_dir) && Files.isDirectory(repo_dir)) {
            final Storage storage = new FileSystemStorage(repo_dir);
            return new Repository(storage);
        }

        return null;


//        final URL packageXmlUri = getClass().getClassLoader().getResource("repos/" + repo + "/.expath-pkg/packages.xml");
//        if (packageXmlUri != null) {
//            try {
//                final Path packageXml = Paths.get(packageXmlUri.toURI());
//                if (Files.exists(packageXml)) {
//                    final Path repo_dir = packageXml.getParent().getParent();
//                    final Storage storage = new FileSystemStorage(repo_dir);
//                    return new Repository(storage);
//                }
//            } catch (final URISyntaxException e) {
//                throw new PackageException(e.getMessage(), e);
//            }
//        }
//
//        return null;
    }

    private Package latestPackage(Repository repo, String name)
    {
        Packages pp = repo.getPackages(name);
        return pp.latest();
    }

    private static class TestSource
            extends StreamSource
    {
        public TestSource(String str) {
            myStr = str;
        }
        @Override
        public String toString() {
            return myStr;
        }
        private final String myStr;
    }

    private static class TestResolver
            extends Storage.PackageResolver
    {
        public TestResolver(String pkg) {
            myPkg = pkg;
        }
        @Override
        public StreamSource resolveComponent(String path) throws PackageException {
            return new TestSource(myPkg + " | component | " + path);
        }
        @Override
        public String getResourceName() {
            throw new UnsupportedOperationException("Not supported in tests.");
        }
        @Override
        public StreamSource resolveResource(String path) throws PackageException {
            throw new UnsupportedOperationException("Not supported in tests.");
        }
        @Override
        public URI getContentDirBaseURI() throws PackageException {
            throw new UnsupportedOperationException("Not supported in tests.");
        }
        private final String myPkg;
    }

    private static final String APP_PKG_NAME   = "http://example.com/my-app";
    private static final String LIB_A_PKG_NAME = "http://example.org/lib-a";
    private static final String LIB_B_PKG_NAME = "http://example.org/lib-b";
    private static final String LIB_X_PKG_NAME = "http://example.org/lib-x";
    private static final String LIB_Y_PKG_NAME = "http://example.org/lib-y";

    /**
     * Use "by-hand-constructed" repository and packages.
     *
     * Other tests parse real repositories directly from the file system.
     */
    @Test
    public void firstSimpleTest()
            throws PackageException
    {
        // the fixture (the repository and packages)
        Repository repo = new Repository();
        Package p1 = new Package(repo, new TestResolver("pkg 1"), "urn:test:1", "p1", "1.0", "title 1", "http://home/1");
        p1.addPublicUri(URISpace.XSLT, "urn:test:1:some.xsl", "dir/some.xsl");
        p1.addPublicUri(URISpace.XSLT, "urn:test:1:other.xsl", "dir/other.xsl");
        repo.addPackage(p1);
        Package p2 = new Package(repo, new TestResolver("pkg 2"), "urn:test:2", "p2", "1.0", "title 2", "http://home/2");
        p2.addPublicUri(URISpace.XSLT, "urn:test:2:some.xsl", "dir/some.xsl");
        p2.addPublicUri(URISpace.XSLT, "urn:test:2:other.xsl", "dir/other.xsl");
        repo.addPackage(p2);
        // the SUT
        CompositeUniverse sut = new CompositeUniverse(true);
        sut.addUniverse(p1);
        sut.addUniverse(p2);
        // do it
        Source src = sut.resolve("urn:test:1:some.xsl", URISpace.XSLT);
        // assert
        assertEquals("the resolved component", "pkg 1 | component | dir/some.xsl", src.toString());
    }

    /**
     * Use one package
     */
    @Test
    public void onePackageDefaultDependencies()
            throws PackageException, URISyntaxException, IOException
    {
        // the fixture
        Repository repo = getRepository("deps-1");
        // the sut
        Package sut = latestPackage(repo, APP_PKG_NAME);
        // do it
        Source src = sut.resolve("http://example.org/lib-x/query", URISpace.XQUERY);
        // assertions
        assertComponent("deps-1", "lib-x", "12.9.0", "query.xql", src);
    }

    @Test
    public void onePackageDefaultDependenciesPackages()
            throws PackageException, URISyntaxException, IOException
    {
        // the fixture
        Repository repo = getRepository("deps-1");
        // the sut
        Packages sut = repo.getPackages(LIB_Y_PKG_NAME);
        // do it
        Source src = sut.resolve("http://example.org/lib-y/query", URISpace.XQUERY);
        // assertions
        assertComponent("deps-1", "lib-y", "1.19.18", "query.xql", src);
    }

    @Test
    public void onePackageTransitiveDependencies()
            throws PackageException, URISyntaxException, IOException
    {
        // the fixture
        Repository repo = getRepository("deps-1");
        // the sut
        Package sut = latestPackage(repo, APP_PKG_NAME);
        // do it
        Source src = sut.resolve("http://example.org/lib-y/style.xsl", URISpace.XSLT, true);
        // assertions
        assertComponent("deps-1", "lib-y", "1.19.18", "style.xsl", src);
    }

    @Test
    public void onePackageTransitiveDependenciesPackages()
            throws PackageException, URISyntaxException, IOException
    {
        // the fixture
        Repository repo = getRepository("deps-1");
        // the sut
        Packages sut = repo.getPackages(APP_PKG_NAME);
        // do it
        Source src = sut.resolve("http://example.org/lib-y/style.xsl", URISpace.XSLT, true);
        // assertions
        assertComponent("deps-1", "lib-y", "1.19.18", "style.xsl", src);
    }

    @Test
    public void onePackageStrictDependencies()
            throws PackageException, URISyntaxException, IOException
    {
        // the fixture
        Repository repo = getRepository("deps-1");
        // the sut
        Package sut = latestPackage(repo, APP_PKG_NAME);
        // do it
        Source src = sut.resolve("http://example.org/lib-y/style.xsl", URISpace.XSLT, false);
        // assertions
        assertNull(src);
    }

    @Test
    public void onePackageStrictDependenciesPackages()
            throws PackageException, URISyntaxException, IOException
    {
        // the fixture
        Repository repo = getRepository("deps-1");
        // the sut
        Packages sut = repo.getPackages(APP_PKG_NAME);
        // do it
        Source src = sut.resolve("http://example.org/lib-y/style.xsl", URISpace.XSLT, false);
        // assertions
        assertNull(src);
    }

    @Test
    public void wholeRepoUniverse()
            throws PackageException, URISyntaxException, IOException
    {
        // the sut
        Repository sut = getRepository("deps-1");
        // do it
        Source src = sut.resolve("http://example.org/lib-b/query", URISpace.XQUERY);
        // assertions
        assertComponent("deps-1", "lib-b", "0.1.0", "query.xql", src);
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
