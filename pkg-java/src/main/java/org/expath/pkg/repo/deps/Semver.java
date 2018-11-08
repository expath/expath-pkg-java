/****************************************************************************/
/*  File:       Semver.java                                                 */
/*  Author:     Adam Retter - Evolved Binary                                */
/*  Date:       2018-11-09                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2018 Adam Retter (see end of file.)                   */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo.deps;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.expath.pkg.repo.PackageException;

/**
 * Represents a SemVer template, or a SemVer version number.
 *
 * @author Adam Retter
 */
public class Semver implements Comparable<Semver> {

    /**
     * https://semver.org/
     * v2.0
     */
    private static final Pattern PTN_SEM_VER = Pattern.compile("([0-9]+)(\\.[0-9]+)?(\\.[0-9]+)?(-[1-9A-Za-z][0-9A-Za-z-]*(?:\\.[1-9A-Za-z][0-9A-Za-z-]*)*)?(\\+[0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*)?");

    private final int majorVersion;
    private final Integer minorVersion;
    private final Integer patchVersion;
    private final String preReleaseVersion;
    private final String buildMetadata;

    public Semver(final int majorVersion, final Integer minorVersion,
            final Integer patchVersion, final String preReleaseVersion,
            final String buildMetadata) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
        this.preReleaseVersion = preReleaseVersion;
        this.buildMetadata = buildMetadata;
    }

    public static Semver parse(final String semver) throws PackageException {
        final Matcher matcher = PTN_SEM_VER.matcher(semver);
        if (!matcher.matches()) {
            parseError(semver, "Does not match EXPath SemVer Regex");
        }

        final int majorVersion = Integer.parseInt(matcher.group(1));
        Integer minorVersion = null;
        Integer patchVersion = null;
        String preReleaseVersion = null;
        String buildMetadata = null;

        for (int i = 2; i <= matcher.groupCount(); i++) {
            final String group = matcher.group(i);
            if (group != null) {
                switch (group.charAt(0)) {
                    case '.':
                        if (minorVersion == null) {
                            minorVersion = Integer.parseInt(group.substring(1));
                        } else {
                            patchVersion = Integer.parseInt(group.substring(1));
                        }
                        break;

                    case '-':
                        preReleaseVersion = group.substring(1);
                        break;

                    case '+':
                        buildMetadata = group.substring(1);
                        break;

                    default:
                        parseError(semver, "Illegal state");
                }
            }
        }

//        if (minorVersion == null) {
//            minorVersion = 0;
//        }
//
//        if (patchVersion == null) {
//            patchVersion = 0;
//        }

        return new Semver(
                majorVersion,
                minorVersion,
                patchVersion,
                preReleaseVersion,
                buildMetadata
        );
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public Integer getMinorVersion() {
        return minorVersion;
    }

    public Integer getPatchVersion() {
        return patchVersion;
    }

    public String getPreReleaseVersion() {
        return preReleaseVersion;
    }

    public String getBuildMetadata() {
        return buildMetadata;
    }

    /**
     * Does {@code rhs} (a SemVer version) match this SemVer template?
     */
    public boolean matches(final Semver rhs)
            throws PackageException {

        return compareTo(rhs) == 0;
    }

    /**
     * Does {@code rhs} (a SemVer version) match this SemVer template as a minimum?
     * <p>
     * Return true if {@code rhs} is equal or above this template.
     */
    public boolean matchesMin(final Semver rhs)
            throws PackageException {
        return compareTo(rhs) <= 0;
    }

    /**
     * Does {@code rhs} (a SemVer version) match this SemVer template as a maximum?
     * <p>
     * Return true if {@code rhs} is equal or below this template.
     */
    public boolean matchesMax(final Semver rhs)
            throws PackageException {
        return compareTo(rhs) >= 0;
    }

    @Override
    public int compareTo(final Semver other) {
        final int majorDiff = this.majorVersion - other.majorVersion;
        if (majorDiff != 0) {
            return majorDiff * 1000;
        }

        if (this.minorVersion != null) {
            final int minorDiff = this.minorVersion - other.minorVersion;
            if (minorDiff != 0) {
                return minorDiff * 100;
            }
        } else {
            return 0;
        }

        if (this.patchVersion != null) {
            final int patchDiff = this.patchVersion - other.patchVersion;
            if (patchDiff != 0) {
                return patchDiff * 10;
            }
        } else {
            return 0;
        }

        if (this.preReleaseVersion == null && other.preReleaseVersion == null) {
            return 0;
        } else if (this.preReleaseVersion == null) {
            return 1;
        } else if (other.preReleaseVersion == null) {
            return -1;
        }

        // both have preReleaseVersion, need to compare them
        final String[] preReleaseIdentfiers = this.preReleaseVersion.split("\\.");
        final String[] otherPreReleaseIdentfiers = other.preReleaseVersion.split("\\.");
        for(int i = 0; i < Math.min(preReleaseIdentfiers.length, otherPreReleaseIdentfiers.length); i++) {
            final String preReleaseIdentfier = preReleaseIdentfiers[i];
            final String otherPreReleaseIdentfier = otherPreReleaseIdentfiers[i];
            final boolean preReleaseIdentfierIsNum = isNumeric(preReleaseIdentfier);
            final boolean otherPreReleaseIdentfierIsNum = isNumeric(otherPreReleaseIdentfier);

            if (preReleaseIdentfierIsNum && otherPreReleaseIdentfierIsNum) {
                // both are numeric
                final int preReleaseIdentifierDiff = Integer.parseInt(preReleaseIdentfier) - Integer.parseInt(otherPreReleaseIdentfier);
                if (preReleaseIdentifierDiff != 0) {
                    return preReleaseIdentifierDiff;
                }
            } else if (preReleaseIdentfierIsNum) {
                return -1;
            } else if (otherPreReleaseIdentfierIsNum) {
                return 1;
            } else {
                // both are alphanumeric
                final int preReleaseIdentifierDiff = preReleaseIdentfier.compareTo(otherPreReleaseIdentfier);
                if (preReleaseIdentifierDiff != 0) {
                    return preReleaseIdentifierDiff;
                }
            }
        }

        return preReleaseIdentfiers.length - otherPreReleaseIdentfiers.length;
    }

    private static boolean isNumeric(final String string) {
        for (int i = 0; i < string.length(); i++) {
            final char c = string.charAt(i);
            if ('0' < c || c > '9') {
                return false;
            }
        }
        return true;
     }

    private static void parseError(final String semver, final String msg)
            throws PackageException {
        throw new PackageException("Invalid SemVer '" + semver + "': " + msg);
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
/*  The Initial Developer of the Original Code is Adam Retter Georges.      */
/*                                                                          */
/*  Contributor(s): none.                                                   */
/* ------------------------------------------------------------------------ */
