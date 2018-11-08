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
 * @author Adam Retter
 */
public class SemverTest
{
    @Test
    public void testParseTemplateMajor() throws PackageException
    {
        Semver v = Semver.parse("99");
        assertEquals("major number", 99, v.getMajorVersion());
        assertNull("minor number", v.getMinorVersion());
        assertNull("patch number", v.getPatchVersion());
        assertNull("pre-release", v.getPreReleaseVersion());
        assertNull("build metadata", v.getBuildMetadata());
    }

    @Test
    public void testParseTemplateMinor() throws PackageException
    {
        Semver v = Semver.parse("1.2");
        assertEquals("major number", 1, v.getMajorVersion());
        assertEquals("minor number", 2, (int)v.getMinorVersion());
        assertNull("patch number", v.getPatchVersion());
        assertNull("pre-release", v.getPreReleaseVersion());
        assertNull("build metadata", v.getBuildMetadata());
    }

    @Test
    public void testParseTemplatePatch() throws PackageException
    {
        Semver v = Semver.parse("1.2.3");
        assertEquals("major number", 1, v.getMajorVersion());
        assertEquals("minor number", 2, (int)v.getMinorVersion());
        assertEquals("patch number", 3, (int)v.getPatchVersion());
        assertNull("pre-release", v.getPreReleaseVersion());
        assertNull("build metadata", v.getBuildMetadata());
    }

    @Test
    public void testParseTemplatePrerelease() throws PackageException
    {
        Semver v = Semver.parse("1.2.3-SNAPSHOT");
        assertEquals("major number", 1, v.getMajorVersion());
        assertEquals("minor number", 2, (int)v.getMinorVersion());
        assertEquals("patch number", 3, (int)v.getPatchVersion());
        assertEquals("pre-release", "SNAPSHOT", v.getPreReleaseVersion());
        assertNull("build metadata", v.getBuildMetadata());
    }

    @Test
    public void testParseTemplateBuildMetadata() throws PackageException
    {
        Semver v = Semver.parse("1.2.3-SNAPSHOT+201811090235");
        assertEquals("major number", 1, v.getMajorVersion());
        assertEquals("minor number", 2, (int)v.getMinorVersion());
        assertEquals("patch number", 3, (int)v.getPatchVersion());
        assertEquals("pre-release", "SNAPSHOT", v.getPreReleaseVersion());
        assertEquals("build metadata", "201811090235", v.getBuildMetadata());
    }

    @Test
    public void testParseTemplateWithZero() throws PackageException
    {
        Semver v = Semver.parse("1.0.3");
        assertEquals("major number", 1, v.getMajorVersion());
        assertEquals("minor number", 0, (int)v.getMinorVersion());
        assertEquals("patch number", 3, (int)v.getPatchVersion());
    }

    @Test
    public void testParseTemplateSpecial() throws PackageException
    {
        Semver v = Semver.parse("1.2.3-beta-4");
        assertEquals("major number", 1, v.getMajorVersion());
        assertEquals("minor number", 2, (int)v.getMinorVersion());
        assertEquals("patch number", 3, (int)v.getPatchVersion());
        assertEquals("pre-release version", "beta-4", v.getPreReleaseVersion());
    }

    @Test
    public void testParseTemplateSpecial2() throws PackageException
    {
        Semver v = Semver.parse("1.0-SNAPSHOT");
        assertEquals("major number", 1, v.getMajorVersion());
        assertEquals("minor number", 0, (int)v.getMinorVersion());
        assertNull("patch number", v.getPatchVersion());
        assertEquals("pre-release version", "SNAPSHOT", v.getPreReleaseVersion());
    }

    @Test(expected = PackageException.class)
    public void testParseInvalid() throws PackageException
    {
        Semver.parse("1.2.3.4");
    }

    @Test
    public void testCompareMajor() throws PackageException {
        final Semver v1 = Semver.parse("1.0.0");
        final Semver v2 = Semver.parse("2.0.0");

        assertEquals(0, v1.compareTo(v1));
        assertEquals(0, v2.compareTo(v2));

        assertTrue(v1.compareTo(v2) < 0);
        assertTrue(v2.compareTo(v1) > 0);
    }

    @Test
    public void testCompareMinor() throws PackageException {
        final Semver v01 = Semver.parse("0.1.0");
        final Semver v02 = Semver.parse("0.2.0");

        assertEquals(0, v01.compareTo(v01));
        assertEquals(0, v02.compareTo(v02));

        assertTrue(v01.compareTo(v02) < 0);
        assertTrue(v02.compareTo(v01) > 0);
    }

    @Test
    public void testComparePatch() throws PackageException {
        final Semver v001 = Semver.parse("0.0.1");
        final Semver v002 = Semver.parse("0.0.2");

        assertEquals(0, v001.compareTo(v001));
        assertEquals(0, v002.compareTo(v002));

        assertTrue(v001.compareTo(v002) < 0);
        assertTrue(v002.compareTo(v001) > 0);
    }

    @Test
    public void testComparePrerelease() throws PackageException {
        final Semver v123_Snapshot = Semver.parse("1.2.3-SNAPSHOT");
        final Semver v123 = Semver.parse("1.2.3");

        assertEquals(0, v123_Snapshot.compareTo(v123_Snapshot));
        assertEquals(0, v123.compareTo(v123));

        assertTrue(v123_Snapshot.compareTo(v123) < 0);
        assertTrue(v123.compareTo(v123_Snapshot) > 0);


        final Semver v123_RC1 = Semver.parse("1.2.3-RC1");
        final Semver v123_RC2 = Semver.parse("1.2.3-RC2");

        assertEquals(0, v123_RC1.compareTo(v123_RC1));
        assertEquals(0, v123_RC2.compareTo(v123_RC2));

        assertTrue(v123_RC1.compareTo(v123_RC2) < 0);
        assertTrue(v123_RC2.compareTo(v123_RC1) > 0);


        final Semver v123_dRC1 = Semver.parse("1.2.3-RC.1");
        final Semver v123_dRC2 = Semver.parse("1.2.3-RC.2");

        assertEquals(0, v123_dRC1.compareTo(v123_dRC1));
        assertEquals(0, v123_dRC2.compareTo(v123_dRC2));

        assertTrue(v123_dRC1.compareTo(v123_dRC2) < 0);
        assertTrue(v123_dRC2.compareTo(v123_dRC1) > 0);


        final Semver v123_dRC11 = Semver.parse("1.2.3-RC.1.1");

        assertEquals(0, v123_dRC11.compareTo(v123_dRC11));

        assertTrue(v123_dRC1.compareTo(v123_dRC11) < 0);
        assertTrue(v123_dRC11.compareTo(v123_dRC1) > 0);
        assertTrue(v123_dRC11.compareTo(v123_dRC2) < 0);
        assertTrue(v123_dRC2.compareTo(v123_dRC11) > 0);
    }

    @Test
    public void testCompareBuildMetadata() throws PackageException {
        final Semver v123 = Semver.parse("1.2.3");
        final Semver v123_b2017 = Semver.parse("1.2.3+2017");
        final Semver v123_b2018 = Semver.parse("1.2.3+2018");

        assertEquals(0, v123.compareTo(v123_b2017));
        assertEquals(0, v123_b2017.compareTo(v123));
        assertEquals(0, v123_b2017.compareTo(v123_b2018));
        assertEquals(0, v123_b2018.compareTo(v123_b2017));
    }

    @Test
    public void testMatchesSubversion() throws PackageException
    {
        Semver template = Semver.parse("1.2");
        Semver version = Semver.parse("1.2.3");
        boolean result = template.matches(version);
        assertTrue("the version matches the template", result);
    }

    @Test
    public void testMatchesMin_1() throws PackageException
    {
        Semver template = Semver.parse("1.2");
        Semver version = Semver.parse("1.2.3");
        boolean result = template.matchesMin(version);
        assertTrue("the version matches the template as minimum", result);
    }

    @Test
    public void testMatchesMin_2() throws PackageException
    {
        Semver template = Semver.parse("1.2.3");
        Semver version = Semver.parse("1.2.3");
        boolean result = template.matchesMin(version);
        assertTrue("the version matches the template as minimum", result);
    }

    @Test
    public void testMatchesMin_3() throws PackageException
    {
        Semver template = Semver.parse("1");
        Semver version = Semver.parse("1.2.3");
        boolean result = template.matchesMin(version);
        assertTrue("the version matches the template as minimum", result);
    }

    @Test
    public void testMatchesMin_4() throws PackageException
    {
        Semver template = Semver.parse("1.2.3-alpha");
        Semver version = Semver.parse("1.2.3");
        boolean result = template.matchesMin(version);
        assertTrue("the version matches the template as minimum", result);
    }

    @Test
    public void testMatchesMin_5() throws PackageException
    {
        Semver template = Semver.parse("1.2");
        Semver version = Semver.parse("1.2.3-alpha");
        boolean result = template.matchesMin(version);
        assertTrue("the version matches the template as minimum", result);
    }

    @Test
    public void testMatchesMin_6() throws PackageException
    {
        Semver template = Semver.parse("1.2.3");
        Semver version = Semver.parse("1.2.3-alpha");
        boolean result = template.matchesMin(version);
        assertFalse("the version matches the template as minimum", result);
    }

    @Test
    public void testMatchesMin_7() throws PackageException
    {
        Semver template = Semver.parse("1.2.3-alpha");
        Semver version = Semver.parse("1.2.3-alpha");
        boolean result = template.matchesMin(version);
        assertTrue("the version matches the template as minimum", result);
    }

    @Test
    public void testMatchesMax_1() throws PackageException
    {
        Semver template = Semver.parse("1.2");
        Semver version = Semver.parse("1.2.3");
        boolean result = template.matchesMax(version);
        assertTrue("the version matches the template as maximum", result);
    }

    @Test
    public void testMatchesMax_2() throws PackageException
    {
        Semver template = Semver.parse("1.2.3");
        Semver version = Semver.parse("1.2.3");
        boolean result = template.matchesMax(version);
        assertTrue("the version matches the template as maximum", result);
    }

    @Test
    public void testMatchesMax_3() throws PackageException
    {
        Semver template = Semver.parse("1");
        Semver version = Semver.parse("1.2.3");
        boolean result = template.matchesMax(version);
        assertTrue("the version matches the template as maximum", result);
    }

    @Test
    public void testMatchesMax_4() throws PackageException
    {
        Semver template = Semver.parse("1.2.3-alpha");
        Semver version = Semver.parse("1.2.3");
        boolean result = template.matchesMax(version);
        assertFalse("the version matches the template as maximum", result);
    }

    @Test
    public void testMatchesMax_5() throws PackageException
    {
        Semver template = Semver.parse("1.2");
        Semver version = Semver.parse("1.2.3-alpha");
        boolean result = template.matchesMax(version);
        assertTrue("the version matches the template as maximum", result);
    }

    @Test
    public void testMatchesMax_6() throws PackageException
    {
        Semver template = Semver.parse("1.2.3");
        Semver version = Semver.parse("1.2.3-alpha");
        boolean result = template.matchesMax(version);
        assertTrue("the version matches the template as maximum", result);
    }

    @Test
    public void testMatchesMax_7() throws PackageException
    {
        Semver template = Semver.parse("1.2.3-alpha");
        Semver version = Semver.parse("1.2.3-alpha");
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
/*  Contributor(s): Adam Retter.                                            */
/* ------------------------------------------------------------------------ */
