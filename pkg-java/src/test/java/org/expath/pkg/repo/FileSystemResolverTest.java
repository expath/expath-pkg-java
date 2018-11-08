/****************************************************************************/
/*  File:       FileSystemResolverTest.java                                 */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2013-12-26                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2013 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link FileSystemStorage.FileSystemResolver}.
 *
 * @author Florent Georges
 */
public class FileSystemResolverTest
{
    @Test
    public void testConstructor()
            throws Exception
    {
        // get the repo
        Path repodir = Paths.get("target/test-classes/repos/simple");
        Storage storage = new FileSystemStorage(repodir);
        Repository repo = new Repository(storage);
        // get the pkg
        Packages packages = repo.getPackages("http://www.example.org/lib/hello");
        Package pkg = packages.latest();
        // the SUT
        Storage.PackageResolver resolver = pkg.getResolver();
        // the base URI must be a file: URI, absolute, with "hello-1.1.1/hello/"
        // resolved against the repo dir
        URI repouri  = repodir.toUri();
        URI expected = repouri.resolve("hello-1.1.1/hello/");
        URI actual   = resolver.getContentDirBaseURI();
        Assert.assertEquals("base URI scheme is file:", "file", actual.getScheme());
        Assert.assertTrue("base URI is absolute", actual.isAbsolute());
        Assert.assertEquals("base URI value", expected, actual);
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
