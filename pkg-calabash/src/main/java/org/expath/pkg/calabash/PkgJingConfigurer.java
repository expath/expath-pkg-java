/****************************************************************************/
/*  File:       PkgJingConfigurer.java                                      */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-10-19                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.calabash;

import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.ValidateProperty;
import com.xmlcalabash.config.JingConfigurer;
import javax.xml.transform.URIResolver;
import org.expath.pkg.repo.Repository;
import org.expath.pkg.repo.URISpace;

/**
 * ...
 *
 * @author Florent Georges
 */
public class PkgJingConfigurer
        implements JingConfigurer
{
    public PkgJingConfigurer(Repository repo)
    {
        myRepo = repo;
    }

    @Override
    public void configRNG(PropertyMapBuilder props)
    {
        URIResolver resolver = new org.expath.pkg.repo.resolver.PkgURIResolver(myRepo, URISpace.RNG);
        props.put(ValidateProperty.URI_RESOLVER, resolver);
    }

    @Override
    public void configRNC(PropertyMapBuilder props)
    {
        URIResolver resolver = new org.expath.pkg.repo.resolver.PkgURIResolver(myRepo, URISpace.RNC);
        props.put(ValidateProperty.URI_RESOLVER, resolver);
    }

    private Repository myRepo;
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
