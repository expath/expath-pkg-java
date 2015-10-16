/****************************************************************************/
/*  File:       PkgJaxpConfigurer.java                                      */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-10-25                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.calabash;

import com.xmlcalabash.config.JaxpConfigurer;
import javax.xml.validation.SchemaFactory;
import org.expath.pkg.repo.resolver.PkgLSResourceResolver;
import org.expath.pkg.repo.Repository;
import org.expath.pkg.repo.URISpace;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * ...
 *
 * @author Florent Georges
 */
public class PkgJaxpConfigurer
        implements JaxpConfigurer
{
    public PkgJaxpConfigurer(Repository repo)
    {
        myRepo = repo;
    }

    @Override
    public void configSchemaFactory(SchemaFactory factory)
    {
        LSResourceResolver r = new PkgLSResourceResolver(myRepo, URISpace.XSD);
        factory.setResourceResolver(r);
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
