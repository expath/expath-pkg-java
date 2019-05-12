/****************************************************************************/
/*  File:       Logger.java                                                 */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2012-02-13                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2012 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo.tools;

import org.slf4j.LoggerFactory;

/**
 * Wrapper around the Java Logging facility.
 *
 * @author Florent Georges
 */
public class Logger
{
    private Logger(final Class c)
    {
        myLogger = LoggerFactory.getLogger(c);
    }

    public static Logger getLogger(Class c)
    {
        return new Logger(c);
    }

    public void trace(String msg, Object... args)
    {
        myLogger.trace(msg, args);
    }

    public void debug(String msg, Object... args)
    {
        myLogger.debug(msg, args);
    }

    public void info(String msg, Object... args)
    {
        myLogger.info(msg, args);
    }

    public void error(String msg, Object... args)
    {
        myLogger.error(msg, args);
    }

    private final org.slf4j.Logger myLogger;
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
