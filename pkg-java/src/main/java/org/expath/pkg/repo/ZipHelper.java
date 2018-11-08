/****************************************************************************/
/*  File:       ZipHelper.java                                              */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-06-15                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Helper classes providing ZIP file services.
 *
 * @author Florent Georges
 */
class ZipHelper
{
    public ZipHelper(XarSource xarSource)
    {
        myXarSource = xarSource;
    }

    /**
     * Unzip a ZIP archive file to a temporary directory.
     *
     * @return
     *         The temporary directory that has been created, as a File object.
     */
    public Path unzipToTmpDir()
            throws IOException
    {
        Path tmpdir = Files.createTempDirectory("expath-pkg");
        tmpdir.toFile().deleteOnExit();

        unzip(tmpdir);

        return tmpdir;
    }

    /**
     * Unzip a ZIP archive file to a destination directory.
     *
     * @param dest_dir
     *         The destination directory for the ZIP content.  It is created if
     *         it does not exist yet (but then its parent directory must exist.)
     */
    public void unzip(Path dest_dir)
            throws IOException
    {
        // preconditions
        if (!Files.exists(dest_dir)) {
            Files.createDirectories(dest_dir);
        } else if ( !Files.isDirectory(dest_dir) ) {
            throw new IOException("Destination is not a directory: " + dest_dir);
        }

        // loop over entries
        try (final ZipInputStream zis = new ZipInputStream(myXarSource.newInputStream())) {
            ZipEntry entry = null;

            while ((entry = zis.getNextEntry()) != null) {
                if ( ! entry.isDirectory() ) {
                    // destination file
                    Path dest = dest_dir.resolve(entry.getName());
                    // create parent dir if needed
                    Path parent = dest.getParent();
                    if ( ! Files.exists(parent) ) {
                        Files.createDirectories(parent);
                    }

                    // copy the entry to the file
                    Files.copy(zis, dest, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private XarSource myXarSource;
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
