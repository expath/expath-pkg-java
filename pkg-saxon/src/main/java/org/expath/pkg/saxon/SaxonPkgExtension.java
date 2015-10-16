/****************************************************************************/
/*  File:       SaxonPkgExtension.java                                      */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2010-09-19                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010-2015 Florent Georges (see end of file.)          */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.saxon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.expath.pkg.repo.DescriptorExtension;
import org.expath.pkg.repo.FileSystemStorage.FileSystemResolver;
import org.expath.pkg.repo.Package;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.PackageInfo;
import org.expath.pkg.repo.Repository;
import org.expath.pkg.repo.Storage;
import org.expath.pkg.repo.parser.XMLStreamHelper;

/**
 * Represent the extension "saxon", configured with "saxon.xml".
 * 
 * The extension descriptor "saxon.xml" must be at the root of the package.  Its
 * format is as following:
 * 
 * <pre>
 * &lt;package xmlns="http://saxon.sf.net/ns/expath-pkg"&gt;
 *    &lt;jar&gt;dir/file.jar&lt;/jar&gt;
 *    &lt;function&gt;org.example.extension.MyFunction&lt;/function&gt;
 *    &lt;xslt&gt;
 *       &lt;import-uri&gt;http://example.org/ns/project/style.xsl&lt;/import-uri&gt;
 *       &lt;file&gt;stylesheet.xsl&lt;/file&gt;
 *    &lt;/xslt&gt;
 *    &lt;xquery&gt;
 *       &lt;namespace&gt;http://example.org/ns/project/lib&lt;/namespace&gt;
 *       &lt;file&gt;query-lib.xql&lt;/file
 *    &lt;/xquery&gt;
 * &lt;/package&gt;
 * </pre>
 * 
 * The elements "jar", "function", "xslt" and "xquery" are optional, repeatable,
 * and can appear in any order.  The element "jar" links to the JAR files, in
 * the content directory, to be included in the classpath.  The element "function"
 * register an extension function by using its fully qualified class name.  The
 * class must extends the Saxon class ExtensionFunctionDefinition.  If it extends
 * the EXPath class EXPathFunctionDefinition, the method setConfiguration() will
 * be called after it has been instantiated.  The elements "xslt" and "xquery"
 * add XSLT stylesheets and XQuery libraries to the package component list.  They
 * are useful when the components are depending on Saxon, like using some Saxon
 * extension instruction or attribute.
 *
 * @author Florent Georges
 */
public class SaxonPkgExtension
        extends DescriptorExtension
{
    public SaxonPkgExtension()
    {
        super("saxon", "saxon.xml");
    }

    @Override
    protected void parseDescriptor(XMLStreamReader parser, Package pkg)
            throws PackageException
    {
        myXSHelper.ensureNextElement(parser, "package");
        SaxonPkgInfo info = new SaxonPkgInfo(pkg);
        try {
            parser.next();
            while ( parser.getEventType() == XMLStreamConstants.START_ELEMENT ) {
                if ( SAXON_PKG_NS.equals(parser.getNamespaceURI()) ) {
                    handleElement(parser, info);
                }
                else {
                    // ignore elements not in the Saxon Pkg namespace
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

    private void handleElement(XMLStreamReader parser, SaxonPkgInfo info)
            throws PackageException
                 , XMLStreamException
    {
        String local = parser.getLocalName();
        if ( "jar".equals(local) ) {
            String jar = myXSHelper.getElementValue(parser);
            info.addJar(jar);
        }
        else if ( "function".equals(local) ) {
            String fun = myXSHelper.getElementValue(parser);
            info.addFunction(fun);
        }
        else if ( "library".equals(local) ) {
            String lib = myXSHelper.getElementValue(parser);
            info.addLibrary(lib);
        }
        else if ( "xslt".equals(local) ) {
            Mapping m = handleMapping(parser, "import-uri");
            info.addXslt(m.href, m.file);
        }
        else if ( "xquery".equals(local) ) {
            // TODO: Handle main modules as well (with "import-uri" instead of
            // "namespace").
            Mapping m = handleMapping(parser, "namespace");
            info.addXQuery(m.href, m.file);
        }
        else if ( "xslt-wrapper".equals(local) ) {
            Mapping m = handleMapping(parser, "import-uri");
            info.addXsltWrapper(m.href, m.file);
        }
        else if ( "xquery-wrapper".equals(local) ) {
            // TODO: Handle main modules as well (with "import-uri" instead of
            // "namespace").
            Mapping m = handleMapping(parser, "namespace");
            info.addXQueryWrapper(m.href, m.file);
        }
        else {
            throw new PackageException("Unknown Saxon component type: " + local);
        }
    }

    private Mapping handleMapping(XMLStreamReader parser, String uri_name)
            throws PackageException, XMLStreamException
    {
        myXSHelper.ensureNextElement(parser, uri_name);
        String href = myXSHelper.getElementValue(parser);
        myXSHelper.ensureNextElement(parser, "file");
        String file = myXSHelper.getElementValue(parser);
        // position to </...> (xslt, xquery, xslt-wrapper or xquery-wrapper)
        parser.next();
        return new Mapping(href, file);
    }

    @Override
    public void install(Repository repo, Package pkg)
            throws PackageException
    {
        // first init and parse the descriptor
        init(repo, pkg);
        // get the freshly created info object
        SaxonPkgInfo info = getInfo(pkg);
        if ( info == null ) {
            // not a Saxon extension
            return;
        }
        // if there are no JAR, nothing to do
        if ( ! info.hasJars() ) {
            return;
        }
        setupClasspath(pkg, info);
    }

    private SaxonPkgInfo getInfo(Package pkg)
            throws PackageException
    {
        PackageInfo info = pkg.getInfo(getName());
        if ( info == null ) {
            return null;
        }
        if ( ! (info instanceof SaxonPkgInfo) ) {
            throw new PackageException("Not a Saxon-specific package info: " + info.getClass());
        }
        return (SaxonPkgInfo) info;
    }

    private void setupClasspath(Package pkg, SaxonPkgInfo info)
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
                try {
                    // ignore the result, just to check if it exists
                    res.resolveComponent(jar);
                }
                catch ( Storage.NotExistException ex ) {
                    throw new PackageException("The Saxon descriptor refers to a JAR file that does not exist: " + jar, ex);
                }
                // add it to classpath.txt
                out.write(jar);
                out.write("\n");
            }
            out.close();
        }
        catch ( IOException ex ) {
            throw new PackageException("Error writing the Saxon classpath file: " + classpath, ex);
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
     * CalabashPkgExtension.
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
        // check [pkg_dir]/.saxon/
        File saxon = classpath.getParentFile();
        if ( saxon.exists() ) {
            if ( ! saxon.isDirectory() ) {
                throw new PackageException("Private dir is not a directory: " + saxon);
            }
        }
        else if ( ! saxon.mkdir() ) {
            throw new PackageException("Impossible to create directory: " + saxon);
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

    public static final String CLASSPATH_FILE = ".saxon/classpath.txt";
    public static final String SAXON_PKG_NS = "http://saxon.sf.net/ns/expath-pkg";
    private final XMLStreamHelper myXSHelper = new XMLStreamHelper(SAXON_PKG_NS);

    private static class Mapping
    {
        public Mapping(String h, String f) {
            href = h;
            file = f;
        }
        public String href;
        public String file;
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
