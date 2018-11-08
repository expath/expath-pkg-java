package org.expath.pkg.repo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;

public class XarUriSource implements XarSource {

    private final URI uri;

    public XarUriSource(final URI uri) {
        this.uri = uri;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public InputStream newInputStream() throws IOException {
        URLConnection connection = uri.toURL().openConnection();
        connection.connect();
        if ( connection instanceof HttpURLConnection) {
            HttpURLConnection hc = (HttpURLConnection) connection;
            int code = hc.getResponseCode();
            if ( code == 404 ) {
                throw new IOException(new Repository.NotFoundException(uri));
            }
            if ( code < 200 || code >= 300 ) {
                String msg = hc.getResponseMessage();
                throw new IOException(new Repository.HttpException(uri, code, msg));
            }
        }
        return connection.getInputStream();
    }
}
