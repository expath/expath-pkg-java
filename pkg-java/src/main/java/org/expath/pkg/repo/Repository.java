/****************************************************************************/
/*  File:       Repository.java                                             */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-10-19                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.repo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.expath.pkg.repo.Storage.PackageResolver;
import org.expath.pkg.repo.parser.DescriptorParser;
import org.expath.pkg.repo.tools.Logger;

/**
 * Represent a standard EXPath package repository structure on the disk.
 *
 * TODO: Because we have a repository descriptor with the list of installed
 * packages (by the spec: .expath-pkg/packages.txt and .expath-pkg/packages.xml),
 * we don't have to parse all the package descriptors in the repository at the
 * instantiation of the object.  We can instead create "fake" packages with
 * only the information from the repository descriptor, and parse the whole
 * package descriptor only when the user ask for that package.
 * 
 * That way, the creation of a Repository object just needs to parse the
 * repository descriptor, and parses the package descriptors only as needed.
 *
 * @author Florent Georges
 */
public class Repository
        implements Universe
{
    public Repository(Storage storage)
            throws PackageException
    {
        LOG.info("Create a new repository with storage: {0}", storage);
        myStorage    = storage;
        myPackages   = new HashMap<String, Packages>();
        myExtensions = new HashMap<String, Extension>();
        // dynamically register extensions from the classpath
        ServiceLoader<Extension> loader = ServiceLoader.load(Extension.class);
        for ( Extension e : loader ) {
            registerExtension(e);
        }
        // TODO: Enable lazy initialization...
        parsePublicUris();
    }

    /**
     * Shortcut for {@code makeDefaultRepo(null)}.
     */
    public static Repository makeDefaultRepo()
            throws PackageException
    {
        return makeDefaultRepo(null);
    }

    /**
     * Return a repository instantiated from default location.
     * 
     * If the parameter is not null, it uses it.  If not, then looks at the
     * system property {@code expath.repo}, and if it is not set at the
     * environment variable {@code EXPATH_REPO}.  This string is interpreted
     * as a directory path, which must point to a repository.
     * 
     * It throws an exception if the the directory does not exist (or is not a
     * directory), or if there is any error creating the repository object from
     * it.
     */
    public static Repository makeDefaultRepo(String dir)
            throws PackageException
    {
        if ( dir == null ) {
            dir = System.getProperty("expath.repo");
        }
        if ( dir == null ) {
            dir = System.getenv("EXPATH_REPO");
        }
        if ( dir != null ) {
            Path f = Paths.get(dir);
            if ( ! Files.exists(f) ) {
                throw new PackageException("Repo directory does not exist: " + dir);
            }
            if ( ! Files.isDirectory(f) ) {
                throw new PackageException("Repo is not a directory: " + dir);
            }
            try {
                Storage storage = new FileSystemStorage(f);
                return new Repository(storage);
            }
            catch ( PackageException ex ) {
                throw new PackageException("Error setting the repo (" + dir + ")", ex);
            }
        }
        return null;
    }

    /**
     * ...
     */
    final public void registerExtension(Extension ext)
            throws PackageException
    {
        if ( ! myExtensions.containsKey(ext.getName()) ) {
            myExtensions.put(ext.getName(), ext);
            ext.init(this, myPackages);
        }
    }

    /**
     * Reload the repository configuration, so parse again the package descriptors.
     */
    public synchronized void reload()
            throws PackageException
    {
        // TODO: Reload extensions as well?
        myPackages = new HashMap<String, Packages>();
        parsePublicUris();
    }

    /**
     * @return The {@link Storage} object this repository is based upon.
     */
    public Storage getStorage()
    {
        return myStorage;
    }

    public Collection<Packages> listPackages()
    {
        return myPackages.values();
    }

    public Packages getPackages(String name)
    {
        return myPackages.get(name);
    }

    /**
     * ...
     *
     * TODO: Must be delegated to the storage!
     */
    public static Repository createRepository(Path dir)
            throws PackageException
    {
        if ( Files.exists(dir) ) {
            // must be a dir and empty, or that's an error
            if ( ! Files.isDirectory(dir) ) {
                throw new PackageException("File exists and is not a directory (" + dir + ")");
            }
            if ( !FileHelper.isEmpty(dir) ) {
                throw new PackageException("Directory exists and is not empty (" + dir + ")");
            }
            // TODO: Add a force option to delete the dir if it exists and is
            // not empty?
        }
        else {
            try {
                Files.createDirectories(dir);
            } catch (final IOException e) {
                throw new PackageException("Error creating the directory (" + dir + ")", e);
            }
        }
        // here, we know 'dir' is a directory and is empty...
        Path priv_dir = dir.resolve(".expath-pkg");
        try {
            Files.createDirectories(priv_dir);
        } catch (final IOException e) {
            throw new PackageException("Error creating the private directory (" + priv_dir + ")", e);
        }
        return new Repository(new FileSystemStorage(dir));
    }

    /**
     * Install a XAR package into this repository, from a URI location.
     *
     * TODO: Anything to delegate to the storage?
     * 
     * @param pkg The package file (typically a {@code *.xar} or {@code *.xaw} file).
     * 
     * @param force If force is false, this is an error if the same package has
     * already been installed in the repository.  If it is true, it is first
     * deleted if existing.
     * 
     * @param interact How the repository interacts with the user.
     *
     * @return The freshly installed package.
     * 
     * @throws PackageException If any error occurs.
     */
    public Package installPackage(URI pkg, boolean force, UserInteractionStrategy interact)
            throws PackageException
    {
        if ( myStorage.isReadOnly() ) {
            throw new PackageException("The storage is read-only, package install not supported");
        }
        // TODO: Must be moved within the storage class (because we are writing on disk)...
        Path downloaded;
        try {
            URLConnection connection = pkg.toURL().openConnection();
            connection.connect();
            if ( connection instanceof HttpURLConnection ) {
                HttpURLConnection hc = (HttpURLConnection) connection;
                int code = hc.getResponseCode();
                if ( code == 404 ) {
                    throw new NotFoundException(pkg);
                }
                if ( code < 200 || code >= 300 ) {
                    String msg = hc.getResponseMessage();
                    throw new HttpException(pkg, code, msg);
                }
            }
            InputStream instream = connection.getInputStream();
            downloaded = Files.createTempFile(Paths.get(pkg.getPath()).getFileName().toString() + '-', "-expath-tmp" +
                    ".xar");
            Files.copy(instream, downloaded);
        }
        catch ( MalformedURLException ex ) {
            throw new OnlineException(pkg, ex);
        }
        catch ( IOException ex ) {
            throw new OnlineException(pkg, ex);
        }
        return installPackage(downloaded, force, interact);
    }

    /**
     * Install a XAR package into this repository.
     *
     * TODO: In case of exception, the temporary dir is not removed, solve that.
     * 
     * @param xar_file  The package file (typically a {@code *.xar} or {@code *.xaw} file).
     * 
     * @param force If force is false, this is an error if the same package has
     * already been installed in the repository.  If it is true, it is first
     * deleted if existing.
     * 
     * @param interact How the repository interacts with the user.
     *
     * @return The freshly installed package.
     * 
     * @throws PackageException If any error occurs.
     */
    public Package installPackage(Path xar_file, boolean force, UserInteractionStrategy interact)
            throws PackageException
    {
        // preconditions
        if ( ! Files.exists(xar_file)) {
            throw new PackageException("Package file does not exist (" + xar_file + ")");
        }
        myStorage.beforeInstall(force, interact);

        // the temporary dir, to unzip the package
        Path tmp_dir = myStorage.makeTempDir("install");

        // unzip in the package in destination dir
        try {
            ZipHelper zip = new ZipHelper(xar_file);
            zip.unzip(tmp_dir);
        }
        catch ( IOException ex ) {
            throw new PackageException("Error unziping the package", ex);
        }
        interact.logInfo("Package unziped to " + tmp_dir);

        // parse the package
        Path desc_f = tmp_dir.resolve("expath-pkg.xml");
        if ( ! Files.exists(desc_f) ) {
            throw new PackageException("Package descriptor does NOT exist in: " + tmp_dir);
        }
        Source desc = new StreamSource(desc_f.toFile());
        // parse the descriptor
        DescriptorParser parser = new DescriptorParser();
        Package pkg = parser.parse(desc, null, myStorage, this);

        // is the package already in the repo?
        String name = pkg.getName();
        String version = pkg.getVersion();
        Packages pp = myPackages.get(name);
        if ( pp != null ) {
            Package p2 = pp.version(version);
            if ( p2 != null ) {
                if ( force || interact.ask("Force override " + name + " - " + version + "?", false) ) {
                    myStorage.remove(p2);
                    pp.remove(p2);
                    if ( pp.latest() == null ) {
                        myPackages.remove(name);
                    }
                }
                else {
                    throw new AlreadyInstalledException(name, version);
                }
            }
        }

        // where to move the temporary dir? (where within the repo)
        String key = pkg.getAbbrev() + "-" + version;
        for ( int i = 1; myStorage.packageKeyExists(key) && i < 100 ; ++i ) {
            key = pkg.getAbbrev() + "-" + version + "__" + i;
        }
        if ( myStorage.packageKeyExists(key) ) {
            String msg = "Impossible to find a non-existing package key in the repo, stopped at: ";
            throw new PackageException(msg + key);
        }

        myStorage.storeInstallDir(tmp_dir, key, pkg);
        if ( pp == null ) {
            pp = new Packages(name);
            myPackages.put(name, pp);
        }
        pp.add(pkg);

        myStorage.updatePackageLists(pkg);

        for ( Extension ext : myExtensions.values() ) {
            ext.install(this, pkg);
        }

        return pkg;
    }

    /**
     * Remove a package from the repository, by name.
     * 
     * If a package with that name does not exist, or if there are several
     * versions installed, this is an error (except if the package does not
     * exist and {@code force} is {@code true}, then simply returns {@code
     * false}).
     * 
     * @param pkg The package name.
     * 
     * @param force To silently ignore a non existing package (simply returns
     * {@code false} in that case).
     * 
     * @param interact How the repository interacts with the user.
     * 
     * @return True if the package has been successfully removed, false if not
     * (false is returned when the user canceled removing interactively, or if
     * the package does not exist and {@code force} is true).
     * 
     * @throws PackageException If any error occurs during removal.
     */
    public boolean removePackage(String pkg, boolean force, UserInteractionStrategy interact)
            throws PackageException
    {
        if ( ! interact.ask("Remove package " + pkg + "?", true) ) {
            return false;
        }
        // delete the package content
        Packages pp = myPackages.get(pkg);
        if ( pp == null ) {
            if ( force ) {
                return false;
            }
            throw new PackageException("The package does not exist: " + pkg);
        }
        if ( pp.packages().size() != 1 ) {
            throw new PackageException("The package has several versions installed: " + pkg);
        }
        Package p = pp.latest();
        myStorage.remove(p);
        pp.remove(p);
        // remove the package from the list
        myPackages.remove(pkg);
        return true;
    }

    /**
     * Remove a package from the repository, by name and version.
     * 
     * If a package with that name and that version does not exist, this is an
     * error, except if the package does not exist and {@code force} is {@code
     * true} (then it simply returns {@code false}).
     * 
     * @param pkg The package name.
     * 
     * @param version  The package version.
     * 
     * @param force To silently ignore a non existing package (simply returns
     * {@code false} in that case).
     * 
     * @param interact How the repository interacts with the user.
     * 
     * @return True if the package has been successfully removed, false if not
     * (false is returned when the user canceled removing interactively, or if
     * the package does not exist and {@code force} is true).
     * 
     * @throws PackageException If any error occurs during removal.
     */
    public boolean removePackage(String pkg, String version, boolean force, UserInteractionStrategy interact)
            throws PackageException
    {
        if ( ! interact.ask("Remove package " + pkg + ", version " + version + "?", true) ) {
            return false;
        }
        // delete the package content
        Packages pp = myPackages.get(pkg);
        if ( pp == null ) {
            if ( force ) {
                return false;
            }
            throw new PackageException("The package does not exist: " + pkg);
        }
        Package p = pp.version(version);
        if ( p == null ) {
            if ( force ) {
                return false;
            }
            throw new PackageException("The version " + version + " does not exist for the package: " + pkg);
        }
        myStorage.remove(p);
        pp.remove(p);
        // remove the package from the list if it was the only version
        if ( pp.latest() == null ) {
            myPackages.remove(pkg);
        }
        return true;
    }

    /**
     * Resolve a URI in this repository, in the specified space, return a File.
     *
     * For each package, use only the latest version.
     *
     * TODO: What about the packages with a versionning scheme which does NOT
     * follow SemVer? (because basically those are not ordered)
     *
     * TODO: And when we want to resolve into a specific version?  For instance
     * when we are evaluating within the context of a specific package, and we
     * want to resolve only in its declared dependencies?  Or at least to use
     * the versionning of its dependencies to guide within which package we
     * should search (instead of taking always the latest systematically).  Same
     * comments for SaxonRepository.
     */
    @Override
    public Source resolve(String href, URISpace space)
            throws PackageException
    {
        LOG.fine("Repository, resolve in {0}: ''{1}''", space, href);
        for ( Packages pp : myPackages.values() ) {
            Package p = pp.latest();
            Source src = p.resolve(href, space);
            if ( src != null ) {
                return src;
            }
        }
        return null;
    }

    @Override
    public Source resolve(String href, URISpace space, boolean transitive)
            throws PackageException
    {
        // transitive or not is meaningless, as anyway the universe is the whole
        // respository (and dependencies are defined within the repo)
        return resolve(href, space);
    }

    /**
     * ...
     */
    private synchronized void parsePublicUris()
            throws PackageException
    {
        // the list of package dirs
        Set<String> packages = myStorage.listPackageDirectories();
        // the parser
        DescriptorParser parser = new DescriptorParser();
        // loop over the packages
        for ( String p : packages ) {
            PackageResolver res = myStorage.makePackageResolver(p, null);
            Source desc;
            try {
                desc = res.resolveResource("expath-pkg.xml");
            }
            catch ( Storage.NotExistException ex ) {
                throw new PackageException("Package descriptor does NOT exist in: " + p, ex);
            }
            try {
                Package pkg = parser.parse(desc, p, myStorage, this);
                addPackage(pkg);
                for ( Extension ext : myExtensions.values() ) {
                    ext.init(this, pkg);
                }
            } catch (PackageException e) {
                // do not abort: package should be ignored
            }
        }
    }

    /**
     * Package-level to be used in tests (to "manually" build a repo).
     */
    void addPackage(Package pkg)
    {
        String name = pkg.getName();
        Packages pp = myPackages.get(name);
        if ( pp == null ) {
            pp = new Packages(name);
            myPackages.put(name, pp);
        }
        pp.add(pkg);
    }

    /**
     * Package-level, only to be used in tests (to "manually" build a repo).
     */
    Repository()
    {
        // nothing, packages will be added "by hand" in tests
        myStorage    = null; // make javac happy, init the final variable
        myPackages   = new HashMap<String, Packages>(); // init the variable, for addPackage()
        myExtensions = new HashMap<String, Extension>();
    }

    /**
     * The storage object to physically access the repository content.
     */
    private final Storage myStorage;
    /**
     * The list of packages in this repository (indexed by name).
     */
    private Map<String, Packages> myPackages;
    /**
     * The registered extensions (indexed by name).
     */
    private Map<String, Extension> myExtensions;
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(Repository.class);

    /**
     * Exception raised when trying to install a package already installed.
     */
    public static class AlreadyInstalledException
            extends PackageException
    {
        public AlreadyInstalledException(String name, String version)
        {
            super("Same version of the package is already installed: " + name + ", " + version);
            myName    = name;
            myVersion = version;
        }

        public String getName()
        {
            return myName;
        }

        public String getVersion()
        {
            return myVersion;
        }

        private final String myName;
        private final String myVersion;
    }

    /**
     * Exception raised when receiving an error when trying to read a package on the web.
     */
    public static class OnlineException
            extends PackageException
    {
        public OnlineException(URI url)
        {
            super("Error downloading the package at URL: " + url);
            myUrl = url;
        }

        public OnlineException(URI url, String msg)
        {
            super(msg);
            myUrl = url;
        }

        public OnlineException(URI url, Exception cause)
        {
            super("Error downloading the package at URL: " + url, cause);
            myUrl = url;
        }

        public URI getUrl()
        {
            return myUrl;
        }

        private final URI myUrl;
    }

    /**
     * Exception raised when receiving 404 when trying to read a package on the web.
     */
    public static class NotFoundException
            extends OnlineException
    {
        public NotFoundException(URI url)
        {
            super(url, "Package not found at URL: " + url);
        }
    }

    /**
     * Exception raised when receiving 404 when trying to read a package on the web.
     */
    public static class HttpException
            extends OnlineException
    {
        public HttpException(URI url, int code, String status)
        {
            super(url, "HTTP error at URL: " + url + ", code: " + code + ", status: " + status);
            myCode   = code;
            myStatus = status;
        }

        public int getCode()
        {
            return myCode;
        }

        public String getStatus()
        {
            return myStatus;
        }

        private final int    myCode;
        private final String myStatus;
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
