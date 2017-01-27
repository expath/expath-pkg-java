/****************************************************************************/
/*  File:       InstallPackage.java                                         */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-11-02                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009-2013 Florent Georges (see end of file.)          */
/* ------------------------------------------------------------------------ */


package functional.run;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.expath.pkg.repo.FileSystemStorage;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.Repository;
import org.expath.pkg.repo.Storage;
import org.expath.pkg.repo.UserInteractionStrategy;
import static org.junit.Assert.*;

/**
 * Functional tests for package install.
 *
 * @author Florent Georges
 */
public class InstallPackage
{
    public void testInstall()
            throws Exception
    {
        testOne(FunctionalTest.HELLO_XAR_OLD, PACKAGES_TXT_CONTENT_OLD, PACKAGES_XML_CONTENT_OLD, "hello-1.1/hello");
        testOne(FunctionalTest.HELLO_XAR_NEW, PACKAGES_TXT_CONTENT_NEW, PACKAGES_XML_CONTENT_NEW, "hello-1.2/content");
    }

    private void testOne(String xar, String txt_content, String xml_content, String content_dir)
            throws Exception
    {
        // the SUT
        File       repo_dir = new File(FunctionalTest.TMP_REPO_DIR);
        Storage    storage  = new FileSystemStorage(repo_dir);
        Repository repo     = new Repository(storage);
        Path       pkg      = Paths.get(xar);
        // do it
        repo.installPackage(pkg, true, new FakeInteract());
        // .expath-pkg/packages.txt
        File packages_txt = new File(repo_dir, ".expath-pkg/packages.txt");
        assertTrue("the file .expath-pkg/packages.txt exist", packages_txt.exists());
        assertEquals("the file .expath-pkg/packages.txt content", txt_content, readFile(packages_txt));
        // .expath-pkg/packages.xml
        File packages_xml = new File(repo_dir, ".expath-pkg/packages.xml");
        assertTrue("the file .expath-pkg/packages.xml exist", packages_xml.exists());
        assertEquals("the file .expath-pkg/packages.xml content", xml_content, readFile(packages_xml));
        // content dir
        File c_dir = new File(repo_dir, content_dir);
        assertTrue("the content dir exist", c_dir.exists());
        // TODO: Write more assertions...
    }

    private String readFile(File f)
            throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        StringBuilder buffer = new StringBuilder();
        String s;
        while ( (s = reader.readLine()) != null ) {
            buffer.append(s);
            buffer.append('\n');
        }
        return buffer.toString();
    }

    private static final String PACKAGES_TXT_CONTENT_OLD =
            "hello-1.1 http://www.example.org/lib/hello 1.1\n";
    private static final String PACKAGES_XML_CONTENT_OLD =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><packages xmlns=\"http://expath.org/ns/repo/packages\">\n"
            + "<package name=\"http://www.example.org/lib/hello\" dir=\"hello-1.1\" version=\"1.1\"/>\n"
            + "</packages>\n";
    private static final String PACKAGES_TXT_CONTENT_NEW =
            "hello-1.1 http://www.example.org/lib/hello 1.1\n"
            + "hello-1.2 http://www.example.org/lib/hello 1.2\n";
    private static final String PACKAGES_XML_CONTENT_NEW =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><packages xmlns=\"http://expath.org/ns/repo/packages\">\n"
            + "<package name=\"http://www.example.org/lib/hello\" dir=\"hello-1.1\" version=\"1.1\"/>\n"
            + "<package name=\"http://www.example.org/lib/hello\" dir=\"hello-1.2\" version=\"1.2\"/>\n"
            + "</packages>\n";

    private static class FakeInteract
            implements UserInteractionStrategy
    {
        public void messageInfo(String msg) throws PackageException {
            System.out.println("INFO: " + msg);
        }
        public void messageError(String msg) throws PackageException {
            System.out.println("ERROR: " + msg);
        }
        public void logInfo(String msg) throws PackageException {
            System.out.println("LOG: " + msg);
        }
        public boolean ask(String prompt, boolean dflt) throws PackageException {
            System.out.println("ASK: " + prompt + " / " + dflt);
            return dflt;
        }
        public String ask(String prompt, String dflt) throws PackageException {
            System.out.println("ASK: " + prompt + " / " + dflt);
            return dflt;
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
