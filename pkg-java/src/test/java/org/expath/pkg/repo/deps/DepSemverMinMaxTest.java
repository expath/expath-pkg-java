package org.expath.pkg.repo.deps;

import org.expath.pkg.repo.PackageException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests of {@code DepSemverMinMax}.
 *
 * @author Duncan Paterson
 */
public class DepSemverMinMaxTest {
    @Test
    public void testMatchesRange_1() throws PackageException {
    final String min = "0.9.9";
    final String max = "1.1.0";
    final String myVersion = "1.0.0";

    final boolean result = new DepSemverMinMax(min, max).isCompatible(myVersion);
    assertTrue("myVersion is within range", result);
    }

    @Test
    public void testMatchesRange_2() throws PackageException {
        final String min = "1.0.0";
        final String max = "1.1.0";
        final String myVersion = "1.0.0";

        final boolean result = new DepSemverMinMax(min, max).isCompatible(myVersion);
        assertTrue("myVersion is min inclusive", result);
    }

    @Test
    public void testMatchesRange_3() throws PackageException {
        final String min = "0.9.9";
        final String max = "1.0.0";
        final String myVersion = "1.0.0";

        final boolean result = new DepSemverMinMax(min, max).isCompatible(myVersion);
        assertTrue("myVersion is max inclusive", result);
    }

    @Test
    public void testMatchesRange_4() throws PackageException {
        final String min = "0.9.9";
        final String max = "1.1.0";
        final String myVersion = "1.2.0";

        final boolean result = new DepSemverMinMax(min, max).isCompatible(myVersion);
        assertFalse("myVersion is not within range", result);
    }

    @Test
    public void testMatchesRange_5() throws PackageException {
        final String min = "0.9.0";
        final String max = "1.0.0-RC1";
        final String myVersion = "1.0.0";

        final boolean result = new DepSemverMinMax(min, max).isCompatible(myVersion);
        assertFalse("rc is not within range of non-rc", result);
    }

    @Test
    public void testMatchesRange_6() throws PackageException {
        final String min = "0.9.0";
        final String max = "1.0.0";
        final String myVersion = "1.0.0-SNAPSHOT";

        final boolean result = new DepSemverMinMax(min, max).isCompatible(myVersion);
        assertTrue("Snapshot is within range", result);
    }

    @Test
    public void testMatchesRange_7() throws PackageException {
        final String min = "0.9.0-SNAPSHOT";
        final String max = "1.0.0-RC1";
        final String myVersion = "1.0.0-SNAPSHOT";

        final boolean result = new DepSemverMinMax(min, max).isCompatible(myVersion);
        assertFalse("Snapshot is not within range", result);
    }
}

