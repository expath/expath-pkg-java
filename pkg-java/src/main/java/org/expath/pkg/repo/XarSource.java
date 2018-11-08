package org.expath.pkg.repo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface XarSource {
    URI getURI();
    boolean isValid();
    InputStream newInputStream() throws IOException;
}
