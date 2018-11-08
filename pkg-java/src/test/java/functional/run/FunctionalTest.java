/****************************************************************************/
/*  File:       FunctionalTests.java                                        */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2010-12-01                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010-2013 Florent Georges (see end of file.)          */
/* ------------------------------------------------------------------------ */


package functional.run;

import org.expath.pkg.repo.FileHelper;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.fail;

/**
 * TODO: ...
 *
 * @author Florent Georges
 */
public class FunctionalTest
{
    public static final String HELLO_XAR_OLD = "../samples/hello-pkg/hello-1.1.xar";
    public static final String HELLO_XAR_NEW = "../samples/hello-pkg/hello-1.2.xar";

    @Test
    public void runFunctionalTests()
            throws Throwable
    {
        // Initialize a new temporary repo.
        Path repo_dir = Files.createTempDirectory("expath-pkg-java-tmp-repo");

        // Run the actual tests...
        InstallPackage test = new InstallPackage();
        test.testInstall(repo_dir);

        // Tear down the temporary repo.
        FileHelper.deleteQuietly(repo_dir);
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
