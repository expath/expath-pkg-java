/****************************************************************************/
/*  File:       PackagesXmlFile.java                                        */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2013-09-17                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2013 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo.tools;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.expath.pkg.repo.Package;
import org.expath.pkg.repo.PackageException;

/**
 * Represent the file [repo]/.expath-pkg/packages.txt.
 *
 * @author Florent Georges
 */
public class PackagesTxtFile
        extends UpdatableFile
{
    public PackagesTxtFile(Path file)
            throws PackageException
    {
        super(file);
    }

    public void addPackage(Package pkg, String pkg_dir)
            throws PackageException
    {
        String pkg_name = pkg.getName();
        String pkg_version = pkg.getVersion();
        try (final StringWriter buffer = new StringWriter()) {
            try (final BufferedReader in = Files.newBufferedReader(myFile)) {
                String line;
                while ((line = in.readLine()) != null) {
                    // ignore "white" lines
                    if (WHITE_LINE_RE.matcher(line).matches()) {
                        continue;
                    }
                    int pos = line.indexOf(' ');
                    String dir = line.substring(0, pos);
                    ++pos;
                    int pos2 = line.indexOf(' ', pos);
                    String name = line.substring(pos, pos2);
                    pos = pos2 + 1;
                    pos2 = line.length();
                    String version = line.substring(pos, pos2);
                    // we don't write the line if either the dir is the same, or if
                    // both the name and the version are the same
                    if (!dir.equals(pkg_dir) && !(name.equals(pkg_name) && version.equals(pkg_version))) {
                        buffer.write(line);
                        buffer.write("\n");
                    }
                }
            }

            buffer.write(pkg_dir);
            buffer.write(" ");
            buffer.write(pkg_name);
            buffer.write(" ");
            buffer.write(pkg_version);
            buffer.write("\n");
            update(buffer);
        }
        catch ( FileNotFoundException ex ) {
            throw new PackageException("File not found: " + myFile, ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error writing the file: " + myFile, ex);
        }
    }

    public void removePackageByDir(String dir)
            throws PackageException
    {
        try (final StringWriter buffer = new StringWriter()) {
            try (final BufferedReader in = Files.newBufferedReader(myFile)) {

                String line;
                while ((line = in.readLine()) != null) {
                    // ignore "white" lines
                    if (WHITE_LINE_RE.matcher(line).matches()) {
                        continue;
                    }
                    int pos = line.indexOf(' ');
                    String d = line.substring(0, pos);
                    // we don't write the line of the dir of the package to remove
                    if (!d.equals(dir)) {
                        buffer.write(line);
                        buffer.write("\n");
                    }
                }
            }
            update(buffer);
        }
        catch ( FileNotFoundException ex ) {
            throw new PackageException("File not found: " + myFile, ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error writing the file: " + myFile, ex);
        }
    }

    /**
     * Return the names of all the package directories, as a set.
     * 
     * TODO: Cache them! (easy now, as all update go through this class)
     */
    public Set<String> parseDirectories()
            throws PackageException
    {
        try (final InputStream stream = Files.newInputStream(myFile)) {
            return parseDirectories(stream);
        }
        catch ( IOException ex ) {
            throw new PackageException("File not found: " + myFile, ex);
        }
    }

    /**
     * Return the names of all the package directories, as a set.
     * 
     * This is a utility method for systems where packages.txt is not stored as
     * an actual file (for instance on classpath storages).
     */
    public static Set<String> parseDirectories(InputStream stream)
            throws PackageException
    {
        Set<String> result = new HashSet<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ( (line = in.readLine()) != null ) {
                // ignore "white" lines
                if ( WHITE_LINE_RE.matcher(line).matches() ) {
                    continue;
                }
                int pos = line.indexOf(' ');
                String dir = line.substring(0, pos);
                result.add(dir);
            }
            return result;
        }
        catch ( IOException ex ) {
            throw new PackageException("Error reading the package list", ex);
        }
    }

    protected void createEmpty(Writer out)
            throws IOException
    {
        out.write("\n");
    }

    private static final Pattern WHITE_LINE_RE = Pattern.compile("^[ \t\n\r]*$");
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
