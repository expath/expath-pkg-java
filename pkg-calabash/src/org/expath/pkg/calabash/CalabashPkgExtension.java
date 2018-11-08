/****************************************************************************/
/*  File:       CalabashPkgExtension.java                                   */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2011-09-06                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2011 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.calabash;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import net.sf.saxon.s9api.QName;
import org.expath.pkg.repo.DescriptorExtension;
import org.expath.pkg.repo.FileSystemStorage.FileSystemResolver;
import org.expath.pkg.repo.Package;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.PackageInfo;
import org.expath.pkg.repo.Repository;
import org.expath.pkg.repo.Storage;
import org.expath.pkg.repo.parser.XMLStreamHelper;

/**
 * Represent the extension "calabash", configured with "calabash.xml".
 * 
 * The extension descriptor "calabash.xml" must be at the root of the package.
 * Its format is as following:
 * 
 * <pre>{@code
 * <package xmlns="http://saxon.sf.net/ns/expath-pkg">
 *    <jar>dir/file.jar</jar>
 *    <step>
 *       <type>{http://example.org/ns/project}my-step-type</type>
 *       <class>org.example.extension.MyStep</class>
 *    </step>
 * </package>
 * }</pre>
 *
 * The elements "jar" and "step" are optional, repeatable, and can appear in any
 * order.  The element "jar" links to the JAR files, in the content directory,
 * to include the classpath.  The element "step" register an extension step by
 * using its fully qualified class name, and a step type name.  The type name is
 * in Clark notation (that is {@code "{namespace}local-name"}). The class must
 * be a suitable class for an extension step for Calabash.
 * 
 * @author Florent Georges
 */
public class CalabashPkgExtension
        extends DescriptorExtension
{
    public CalabashPkgExtension()
    {
        super("calabash", "calabash.xml");
    }

    @Override
    protected void parseDescriptor(XMLStreamReader parser, Package pkg)
            throws PackageException
    {
        myXSHelper.ensureNextElement(parser, "package");
        CalabashPkgInfo info = new CalabashPkgInfo(pkg);
        try {
            parser.next();
            while ( parser.getEventType() == XMLStreamConstants.START_ELEMENT ) {
                if ( CALABASH_PKG_NS.equals(parser.getNamespaceURI()) ) {
                    handleElement(parser, info);
                }
                else {
                    // ignore elements not in the Calabash Pkg namespace
                    // TODO: FIXME: Actually ignore (pass it.)
                    throw new PackageException("TODO: Ignore elements in other namespace");
                }
                parser.next();
            }
            // position to </package>
            parser.next();
        }
        catch ( XMLStreamException ex ) {
            throw new PackageException("Error reading the saxon descriptor", ex);
        }
        pkg.addInfo(getName(), info);
    }

    private void handleElement(XMLStreamReader parser, CalabashPkgInfo info)
            throws PackageException
                 , XMLStreamException
    {
        String local = parser.getLocalName();
        if ( "jar".equals(local) ) {
            String jar = myXSHelper.getElementValue(parser);
            info.addJar(jar);
        }
        else if ( "step".equals(local) ) {
            myXSHelper.ensureNextElement(parser, "type");
            String type = myXSHelper.getElementValue(parser);
            myXSHelper.ensureNextElement(parser, "class");
            String clazz = myXSHelper.getElementValue(parser);
            // position to </step>
            parser.next();
            // push the values in info
            QName qname = QName.fromClarkName(type);
            info.addStep(qname, clazz);
        }
        else {
            throw new PackageException("Unknown Calabash component type: " + local);
        }
    }

    @Override
    public void install(Repository repo, Package pkg)
            throws PackageException
    {
        // first init and parse the descriptor
        init(repo, pkg);
        // get the freshly created info object
        CalabashPkgInfo info = getInfo(pkg);
        if ( info == null ) {
            // not a Calabash extension
            return;
        }
        // if there are no JAR, nothing to do
        if ( ! info.hasJars() ) {
            return;
        }
        // if there is some JAR, crete the classpath file
        setupClasspath(pkg, info);
    }

    private CalabashPkgInfo getInfo(Package pkg)
            throws PackageException
    {
        PackageInfo info = pkg.getInfo(getName());
        if ( info == null ) {
            return null;
        }
        if ( ! (info instanceof CalabashPkgInfo) ) {
            throw new PackageException("Not a Calabash-specific package info: " + info.getClass());
        }
        return (CalabashPkgInfo) info;
    }

    private void setupClasspath(Package pkg, CalabashPkgInfo info)
            throws PackageException
    {
        File classpath = createClasspathFile(pkg);
        if ( classpath == null ) {
            return;
        }
        Storage.PackageResolver res = pkg.getResolver();
        try {
            FileWriter out = new FileWriter(classpath);
            for ( String jar : info.getJars() ) {
                Source jar_src = res.resolveComponent(jar);
                String sysid = jar_src.getSystemId();
                URI uri = URI.create(sysid);
                File file = new File(uri);
                out.write(file.getCanonicalPath());
                out.write("\n");
            }
            out.close();
        }
        catch ( Storage.NotExistException ex ) {
            throw new PackageException("The Calabash descriptor refers to an inexistent JAR", ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error writing the Calabash classpath file: " + classpath, ex);
        }
    }

    /**
     * Create and return the classpath, return null if already exists.
     * 
     * Throw an exception if the storage is not on the file system, if the
     * parent directory exists but is not in fact a directory, or if it is not
     * possible to create the parent directory.
     * 
     * TODO: FIXME: This code should be moved on the storage class, so we
     * wouldn't have to do all those checks above.  Duplicated in
     * SaxonPkgExtension.
     */
    private File createClasspathFile(Package pkg)
            throws PackageException
    {
        try {
            pkg.getResolver().resolveResource(CLASSPATH_FILE);
            // if the file exists, return null
            return null;
        }
        catch ( Storage.NotExistException ex ) {
            // if the file does not exist, continue
        }
        // if the classpath does not exist, we must use a FileSystemResolver
        FileSystemResolver res = getFileSystemResolver(pkg);
        File classpath = res.resolveResourceAsFile(CLASSPATH_FILE).toFile();
        // check [pkg_dir]/.calabash/
        File calabash = classpath.getParentFile();
        if ( calabash.exists() ) {
            if ( ! calabash.isDirectory() ) {
                throw new PackageException("Private dir is not a directory: " + calabash);
            }
        }
        else if ( ! calabash.mkdir() ) {
            throw new PackageException("Impossible to create directory: " + calabash);
        }
        return classpath;
    }

    private FileSystemResolver getFileSystemResolver(Package pkg)
            throws PackageException
    {
        Storage.PackageResolver res = pkg.getResolver();
        if ( res == null ) {
            throw new PackageException("Resolver is null on package: " + pkg.getName());
        }
        if ( ! (res instanceof FileSystemResolver) ) {
            throw new PackageException("Not a file system resolver: " + res.getClass());
        }
        return (FileSystemResolver) res;
    }

    public static final String CLASSPATH_FILE = ".calabash/classpath.txt";
    public static final String CALABASH_PKG_NS = "http://xmlcalabash.com/ns/expath-pkg";
    private final XMLStreamHelper myXSHelper = new XMLStreamHelper(CALABASH_PKG_NS);
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
