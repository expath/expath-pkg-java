/****************************************************************************/
/*  File:       CalabashPkgInfo.java                                        */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2011-09-06                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2011 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.calabash;

import com.xmlcalabash.core.XProcConfiguration;
import com.xmlcalabash.core.XProcRuntime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.QName;
import org.expath.pkg.repo.Package;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.PackageInfo;
import org.expath.pkg.repo.URISpace;

/**
 * TODO: ...
 *
 * @author Florent Georges
 */
public class CalabashPkgInfo
        extends PackageInfo
{
    public CalabashPkgInfo(Package pkg)
    {
        super("calabash", pkg);
    }

    public void registerExtensionSteps(XProcRuntime runtime)
            throws PackageException
    {
        XProcConfiguration config = runtime.getConfiguration();
        for ( Map.Entry<QName, String> e : mySteps.entrySet() ) {
            QName  key   = e.getKey();
            String value = e.getValue();
            Class clazz = loadClass(value);
            config.implementations.put(key, clazz);
        }
    }

    @Override
    public StreamSource resolve(String href, URISpace space)
            throws PackageException
    {
        // for now, don't resolve anything particular for Calabash
        return null;
    }

    public boolean hasJars()
    {
        return ! myJars.isEmpty();
    }

    public Set<String> getJars()
    {
        return myJars;
    }

    public void addJar(String jar)
    {
        myJars.add(jar);
    }

    public void addStep(QName type, String clazz)
    {
        mySteps.put(type, clazz);
    }

    private Class loadClass(String name)
            throws PackageException
    {
        ClassLoader loader = CalabashPkgInfo.class.getClassLoader();
        try {
            return loader.loadClass(name);
        }
        catch ( ClassNotFoundException ex ) {
            throw new PackageException("Class not found: '" + name + "'", ex);
        }
    }

    private Set<String>        myJars = new HashSet<String>();
    private Map<QName, String> mySteps = new HashMap<QName, String>();
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
