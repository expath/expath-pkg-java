/****************************************************************************/
/*  File:       SaxonRepository.java                                        */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2010-05-11                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010-2015 Florent Georges (see end of file.)          */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.saxon;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import net.sf.saxon.Configuration;
import net.sf.saxon.lib.ModuleURIResolver;
import org.expath.pkg.repo.*;
import org.expath.pkg.repo.Package;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrap an EXPath repository with Saxon-specific services.
 *
 * This handle for instance the extension functions written specifically for
 * Saxon in Java.
 *
 * TODO: Document the "override" mechanism.
 *
 * @author Florent Georges
 */
public class SaxonRepository
{
    public SaxonRepository(final Storage storage)
    {
        this(new Repository(storage));
    }

    public SaxonRepository(final Repository parent)
    {
        myParent = parent;
    }

    /**
     * Initialise the repository.
     *
     * @return any package exceptions that occur whilst trying to find the packages.
     */
    public List<PackageException> init() {
        final List<PackageException> exceptions = new ArrayList<>();
        exceptions.addAll(myParent.init());

        try {
            myParent.registerExtension(new SaxonPkgExtension());
        } catch (final PackageException e) {
            exceptions.add(e);
        }

        return exceptions;
    }

    public Repository getUnderlyingRepo()
    {
        return myParent;
    }

    /**
     * Install a package from a file.
     * 
     * @param pkg The package file (typically a {@code *.xar} or {@code *.xaw} file).
     * 
     * @param force If false, an error is thrown if the package already exists.
     * If true, the package is first deleted from the repository if it already exists.
     * 
     * @param interact How the repository interacts with the user.
     * 
     * @return The freshly installed package.
     * 
     * @throws PackageException If any error occurs.
     */
    public Package installPackage(File pkg, boolean force, UserInteractionStrategy interact)
            throws PackageException
    {
        return myParent.installPackage(new XarFileSource(pkg.toPath()), force, interact);
    }

    /**
     * Install a package from a URI location.
     * 
     * @param pkg The package file (typically a {@code *.xar} or {@code *.xaw} file).
     * 
     * @param force If false, an error is thrown if the package already exists.
     * If true, the package is first deleted from the repository if it already exists.
     * 
     * @param interact How the repository interacts with the user.
     * 
     * @return The freshly installed package.
     * 
     * @throws PackageException If any error occurs.
     */
    public Package installPackage(URI pkg, boolean force, UserInteractionStrategy interact)
            throws PackageException
    {
        return myParent.installPackage(pkg, force, interact);
    }

    /**
     * Remove a package from the repository, by name.
     * 
     * If a package with that name does not exist, or if there are several
     * versions installed, this is an error (except if the package does not
     * exist and {@code force} is {@code true}, then simply returns {@code
     * false}).
     * 
     * @param pkg The package name.
     * 
     * @param force To silently ignore a non existing package (simply returns
     * {@code false} in that case).
     * 
     * @param interact How the repository interacts with the user.
     * 
     * @return True if the package has been successfully removed, false if not
     * (false is returned when the user canceled removing interactively, or if
     * the package does not exist and {@code force} is true).
     * 
     * @throws PackageException If any error occurs during removal.
     */
    public boolean removePackage(String pkg, boolean force, UserInteractionStrategy interact)
            throws PackageException
    {
        return myParent.removePackage(pkg, force, interact);
    }

    /**
     * Resolve a Saxon-specific stuff in this repository, in the specified space, return a File.
     *
     * TODO: FIXME: To handle empty wrappers, just forget about the generated
     * files and so on.  Just return a StreamSource over a StringReader over
     * a generated string with the empty stylesheet or query module.  That
     * small enough to be all dealt in memory, without filesystem access.  Even
     * better! And no need for install step!
     *
     * TODO: Regarding versionning, see comments of {@link Repository#resolve(String,URISpace)}
     * (AKA we don't always want the latest version).
     *
     * @param href the URI
     * @param space the URI space
     *
     * @return the source
     *
     * @throws PackageException if an error occurs
     */
    public Source resolve(String href, URISpace space)
            throws PackageException
    {
        return myParent.resolve(href, space);
    }

    public ModuleURIResolver getModuleURIResolver()
            throws PackageException
    {
        Map<String, String> overrides = getOverrides();
        return new PkgModuleResolver(overrides, this, myParent);
    }

    // Is 'space' required?  Shouldn't that be always XSLT?  Do we want to
    // support SCHEMATRON as well?  If we keep 'space', do check this is an
    // acceptable value.
    public URIResolver getURIResolver(URISpace space)
            throws PackageException
    {
        Map<String, String> overrides = getOverrides();
        URIResolver parent = new org.expath.pkg.repo.resolver.PkgURIResolver(myParent, space);
        return new PkgURIResolver(overrides, this, parent, space);
    }

    /**
     * TODO: Regarding versionning, see comments of {@link Repository#resolve(String,URISpace)}
     * (AKA we don't always want the latest version).
     *
     * @param config the configuration
     *
     * @throws PackageException if an error occurs
     */
    public void registerExtensionFunctions(Configuration config)
            throws PackageException
    {
        for ( Packages pp : myParent.listPackages() ) {
            Package pkg = pp.latest();
            SaxonPkgInfo info = (SaxonPkgInfo) pkg.getInfo("saxon");
            if ( info != null ) {
                info.registerExtensionFunctions(config);
            }
        }
    }

    private static synchronized Map<String, String> getOverrides()
            throws PackageException
    {
        if ( OVERRIDES == null ) {
            OVERRIDES = parseOverrideProperty();
        }
        return OVERRIDES;
    }

    /**
     * @throws PackageException if an error occurs
     */
    private static Map<String, String> parseOverrideProperty()
            throws PackageException
    {
        Map<String, String> res = new HashMap<String, String>();
        String prop = System.getProperty(OVERRIDE_PROP);
        if ( prop == null || "".equals(prop) ) {
            return res;
        }
        LOG.debug("Property {} value is: {}", OVERRIDE_PROP, prop);
        for ( String override : prop.split(",") ) {
            // ignore empty one (so allow the prop ",something else")
            if ( override == null || "".equals(override) ) {
                continue;
            }
            String[] split = override.split("\\|");
            if ( split == null || split.length != 2 ) {
                throw new PackageException("Wrong resolve value: " + prop);
            }
            res.put(split[0], split[1]);
        }
        return res;
    }

    /** The wrapped EXPath repository. */
    private Repository myParent;

    /** The system property name for the overrides. */
    private static final String OVERRIDE_PROP = "org.expath.pkg.saxon.resolve";
    /** The cached overrides map. */
    private static Map<String, String> OVERRIDES = null;
    /** The logger. */
    private static final Logger LOG = LoggerFactory.getLogger(SaxonRepository.class);
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
