/****************************************************************************/
/*  File:       SemverTest.java                                             */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2010-11-15                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2010-2013 Florent Georges (see end of file.)          */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo.deps;

import org.expath.pkg.repo.PackageException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests of {@code Semver}.
 *
 * @author Florent Georges
 */
public class SemverTest
{
    @Test
    public void testParseTemplateMajor() throws PackageException
    {
        String[] parts = Semver.parse("1");
        assertEquals("number of parts", 1, parts.length);
        assertEquals("major number", "1", parts[0]);
    }

    @Test
    public void testParseTemplateMinor() throws PackageException
    {
        String[] parts = Semver.parse("1.2");
        assertEquals("number of parts", 2, parts.length);
        assertEquals("major number", "1", parts[0]);
        assertEquals("major number", "2", parts[1]);
    }

    @Test
    public void testParseTemplatePatch() throws PackageException
    {
        String[] parts = Semver.parse("1.2.3");
        assertEquals("number of parts", 3, parts.length);
        assertEquals("major number", "1", parts[0]);
        assertEquals("major number", "2", parts[1]);
        assertEquals("major number", "3", parts[2]);
    }

    @Test
    public void testParseTemplateWithZero() throws PackageException
    {
        String[] parts = Semver.parse("1.0.3");
        assertEquals("number of parts", 3, parts.length);
        assertEquals("major number", "1", parts[0]);
        assertEquals("major number", "0", parts[1]);
        assertEquals("major number", "3", parts[2]);
    }

    @Test
    public void testParseTemplateSpecial() throws PackageException
    {
        String[] parts = Semver.parse("1.2.3beta-4");
        assertEquals("number of parts", 4, parts.length);
        assertEquals("major number", "1", parts[0]);
        assertEquals("major number", "2", parts[1]);
        assertEquals("major number", "3", parts[2]);
        assertEquals("major number", "beta-4", parts[3]);
    }

    @Test(expected = PackageException.class)
    public void testTooMuchParts() throws PackageException
    {
        Semver.parse("1.2.3.4");
    }

    @Test
    public void testMatchesSubversion() throws PackageException
    {
        Semver template = new Semver("1.2");
        Semver version = new Semver("1.2.3");
        boolean result = template.matches(version);
        assertTrue("the version matches the template", result);
    }

    @Test
    public void testMatchesMin_1() throws PackageException
    {
        Semver template = new Semver("1.2");
        Semver version = new Semver("1.2.3");
        boolean result = template.matchesMin(version);
        assertTrue("the version matches the template as minimum", result);
    }

    @Test
    public void testMatchesMin_2() throws PackageException
    {
        Semver template = new Semver("1.2.3");
        Semver version = new Semver("1.2.3");
        boolean result = template.matchesMin(version);
        assertTrue("the version matches the template as minimum", result);
    }

    @Test
    public void testMatchesMin_3() throws PackageException
    {
        Semver template = new Semver("1");
        Semver version = new Semver("1.2.3");
        boolean result = template.matchesMin(version);
        assertTrue("the version matches the template as minimum", result);
    }

    @Test
    public void testMatchesMin_4() throws PackageException
    {
        Semver template = new Semver("1.2.3alpha");
        Semver version = new Semver("1.2.3");
        boolean result = template.matchesMin(version);
        assertTrue("the version matches the template as minimum", result);
    }

    @Test
    public void testMatchesMin_5() throws PackageException
    {
        Semver template = new Semver("1.2");
        Semver version = new Semver("1.2.3alpha");
        boolean result = template.matchesMin(version);
        assertTrue("the version matches the template as minimum", result);
    }

    @Test
    public void testMatchesMin_6() throws PackageException
    {
        Semver template = new Semver("1.2.3");
        Semver version = new Semver("1.2.3alpha");
        boolean result = template.matchesMin(version);
        assertFalse("the version matches the template as minimum", result);
    }

    @Test
    public void testMatchesMin_7() throws PackageException
    {
        Semver template = new Semver("1.2.3alpha");
        Semver version = new Semver("1.2.3alpha");
        boolean result = template.matchesMin(version);
        assertTrue("the version matches the template as minimum", result);
    }

    @Test
    public void testMatchesMax_1() throws PackageException
    {
        Semver template = new Semver("1.2");
        Semver version = new Semver("1.2.3");
        boolean result = template.matchesMax(version);
        assertTrue("the version matches the template as maximum", result);
    }

    @Test
    public void testMatchesMax_2() throws PackageException
    {
        Semver template = new Semver("1.2.3");
        Semver version = new Semver("1.2.3");
        boolean result = template.matchesMax(version);
        assertTrue("the version matches the template as maximum", result);
    }

    @Test
    public void testMatchesMax_3() throws PackageException
    {
        Semver template = new Semver("1");
        Semver version = new Semver("1.2.3");
        boolean result = template.matchesMax(version);
        assertTrue("the version matches the template as maximum", result);
    }

    @Test
    public void testMatchesMax_4() throws PackageException
    {
        Semver template = new Semver("1.2.3alpha");
        Semver version = new Semver("1.2.3");
        boolean result = template.matchesMax(version);
        assertFalse("the version matches the template as maximum", result);
    }

    @Test
    public void testMatchesMax_5() throws PackageException
    {
        Semver template = new Semver("1.2");
        Semver version = new Semver("1.2.3alpha");
        boolean result = template.matchesMax(version);
        assertTrue("the version matches the template as maximum", result);
    }

    @Test
    public void testMatchesMax_6() throws PackageException
    {
        Semver template = new Semver("1.2.3");
        Semver version = new Semver("1.2.3alpha");
        boolean result = template.matchesMax(version);
        assertTrue("the version matches the template as maximum", result);
    }

    @Test
    public void testMatchesMax_7() throws PackageException
    {
        Semver template = new Semver("1.2.3alpha");
        Semver version = new Semver("1.2.3alpha");
        boolean result = template.matchesMax(version);
        assertTrue("the version matches the template as maximum", result);
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
