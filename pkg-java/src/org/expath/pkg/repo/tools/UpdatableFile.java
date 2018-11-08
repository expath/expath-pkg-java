/****************************************************************************/
/*  File:       UpdatableFile.java                                          */
/*  Author:     F. Georges - H2O Consulting                                 */
/*  Date:       2013-09-17                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2013 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import org.expath.pkg.repo.PackageException;

/**
 * A file that can be updated.
 *
 * @author Florent Georges
 */
public abstract class UpdatableFile
{
    public UpdatableFile(Path file)
            throws PackageException
    {
        myFile = file;
        if ( ! Files.exists(myFile) ) {
            topCreateEmpty();
        }
    }

    /**
     * Create an empty file.
     * 
     * By empty, means without any record.  If this is an XML file for instance,
     * it might have the root element.  No need to close the writer, it is done
     * automatically.
     */
    protected abstract void createEmpty(Writer out)
            throws IOException;

    /**
     * Replace the actual file with {@code content}.
     */
    protected void update(StringWriter content)
            throws PackageException
    {
        try (final OutputStream out = Files.newOutputStream(myFile)) {
            byte[] bytes = content.getBuffer().toString().getBytes();
            out.write(bytes);
        }
        catch ( FileNotFoundException ex ) {
            throw new PackageException("File not found: " + myFile, ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error writing the file: " + myFile, ex);
        }
    }

    /**
     * Wrap the call to {@link #createEmpty(java.io.Writer)}, and handle I/O exceptions.
     */
    private void topCreateEmpty()
            throws PackageException
    {
        try (final Writer out = Files.newBufferedWriter(myFile)) {
            createEmpty(out);
        }
        catch ( FileNotFoundException ex ) {
            throw new PackageException("Impossible to create the packages text list: " + myFile, ex);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error creating the packages text list: " + myFile, ex);
        }
    }

    /** The actual file object. */
    protected Path myFile;
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
