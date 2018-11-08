package org.expath.pkg.repo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class XarFileSource implements XarSource {

    private final Path xarFile;

    public XarFileSource(final Path xarFile) {
        this.xarFile = xarFile;
    }

    @Override
    public URI getURI() {
        return xarFile.toUri();
    }

    @Override
    public boolean isValid() {
        return Files.exists(xarFile) && Files.isDirectory(xarFile);
    }

    @Override
    public InputStream newInputStream() throws IOException {
        return Files.newInputStream(xarFile);
    }
}
