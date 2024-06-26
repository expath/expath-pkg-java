/****************************************************************************/
/*  File:       ConfigurationProxy.java                                     */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:                                                                   */
/*  Tags:                                                                   */
/*      Copyright (c) 2013 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.saxon.tools;

import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import net.sf.saxon.Configuration;
import net.sf.saxon.event.FilterFactory;
import net.sf.saxon.event.Outputter;
import net.sf.saxon.event.PipelineConfiguration;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.expr.PendingUpdateList;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.instruct.*;
import net.sf.saxon.expr.parser.PathMap.PathMapRoot;
import net.sf.saxon.functions.FunctionLibraryList;
import net.sf.saxon.functions.IntegratedFunctionLibrary;
import net.sf.saxon.lib.*;
import net.sf.saxon.om.*;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.s9api.Location;
import net.sf.saxon.serialize.charcode.CharacterSetFactory;
import net.sf.saxon.str.UnicodeString;
import net.sf.saxon.trans.*;
import net.sf.saxon.tree.util.DocumentNumberAllocator;
import net.sf.saxon.type.MissingComponentException;
import net.sf.saxon.type.SchemaDeclaration;
import net.sf.saxon.type.SchemaException;
import net.sf.saxon.type.SchemaType;
import net.sf.saxon.type.SimpleType;
import net.sf.saxon.type.ValidationException;
import org.xml.sax.XMLReader;

/**
 *
 * @author Florent Georges
 */
@Deprecated
public class ConfigurationProxy
        extends Configuration
{
    public ConfigurationProxy(Configuration config)
    {
        myConfig = config;
    }

    @Override
    public synchronized void reuseStyleParser(XMLReader parser)
    {
        myConfig.reuseStyleParser(parser);
    }

    @Override
    public String getStyleParserClass()
    {
        System.err.println("WTF?");
        if ( myConfig == null ) {
            return super.getStyleParserClass();
        }
        return myConfig.getStyleParserClass();
    }

    @Override
    public synchronized XMLReader getStyleParser() throws TransformerFactoryConfigurationError {
        System.err.println("WTF? yo");
        if ( myConfig == null ) {
            return super.getStyleParser();
        }
        return myConfig.getStyleParser();
    }

    @Override
    public SimpleType validateAttribute(StructuredQName name, UnicodeString value, int validation) throws ValidationException, MissingComponentException {
        if ( myConfig == null ) {
            return super.validateAttribute(name, value, validation);
        }
        return myConfig.validateAttribute(name, value, validation);
    }

    @Override
    public NodeInfo unravel(Source source) {
        if ( myConfig == null ) {
            return super.unravel(source);
        }
        return myConfig.unravel(source);
    }

    @Override
    public void setXMLVersion(int version) {
        if ( myConfig == null ) {
            super.setXMLVersion(version);
            return;
        }
        myConfig.setXMLVersion(version);
    }

    @Override
    public void setXIncludeAware(boolean state) {
        if ( myConfig == null ) {
            super.setXIncludeAware(state);
            return;
        }
        myConfig.setXIncludeAware(state);
    }

    @Override
    public void setVersionWarning(boolean warn) {
        if ( myConfig == null ) {
            super.setVersionWarning(warn);
            return;
        }
        myConfig.setVersionWarning(warn);
    }

    @Override
    public void setValidationWarnings(boolean warn) {
        if ( myConfig == null ) {
            super.setValidationWarnings(warn);
            return;
        }
        myConfig.setValidationWarnings(warn);
    }

    @Override
    public void setValidation(boolean validation) {
        if ( myConfig == null ) {
            super.setValidation(validation);
            return;
        }
        myConfig.setValidation(validation);
    }

    @Override
    public void setTreeModel(int treeModel) {
        if ( myConfig == null ) {
            super.setTreeModel(treeModel);
            return;
        }
        myConfig.setTreeModel(treeModel);
    }

    @Override
    public void setTraceListenerClass(String className) {
        if ( myConfig == null ) {
            super.setTraceListenerClass(className);
            return;
        }
        myConfig.setTraceListenerClass(className);
    }

    @Override
    public void setTraceListener(TraceListener traceListener) {
        if ( myConfig == null ) {
            super.setTraceListener(traceListener);
            return;
        }
        myConfig.setTraceListener(traceListener);
    }

    @Override
    public void setTiming(boolean timing) {
        if ( myConfig == null ) {
            super.setTiming(timing);
            return;
        }
        myConfig.setTiming(timing);
    }

    @Override
    public void setStyleParserClass(String parser) {
        if ( myConfig == null ) {
            super.setStyleParserClass(parser);
            return;
        }
        myConfig.setStyleParserClass(parser);
    }

    @Override
    public void setSourceResolver(SourceResolver resolver) {
        if ( myConfig == null ) {
            super.setSourceResolver(resolver);
            return;
        }
        myConfig.setSourceResolver(resolver);
    }

    @Override
    public void setSourceParserClass(String sourceParserClass) {
        if ( myConfig == null ) {
            super.setSourceParserClass(sourceParserClass);
            return;
        }
        myConfig.setSourceParserClass(sourceParserClass);
    }

    @Override
    public void setSerializerFactory(SerializerFactory factory) {
        if ( myConfig == null ) {
            super.setSerializerFactory(factory);
            return;
        }
        myConfig.setSerializerFactory(factory);
    }

    @Override
    public void setSchemaValidationMode(int validationMode) {
        if ( myConfig == null ) {
            super.setSchemaValidationMode(validationMode);
            return;
        }
        myConfig.setSchemaValidationMode(validationMode);
    }

    @Override
    public void setProcessor(ApiProvider processor) {
        if ( myConfig == null ) {
            super.setProcessor(processor);
            return;
        }
        myConfig.setProcessor(processor);
    }

    @Override
    public void setParameterizedURIResolver() {
        if ( myConfig == null ) {
            super.setParameterizedURIResolver();
            return;
        }
        myConfig.setParameterizedURIResolver();
    }

    @Override
    public void setOutputURIResolver(OutputURIResolver outputURIResolver) {
        if ( myConfig == null ) {
            super.setOutputURIResolver(outputURIResolver);
            return;
        }
        myConfig.setOutputURIResolver(outputURIResolver);
    }

    @Override
    public void setNamePool(NamePool targetNamePool) {
        if ( myConfig == null ) {
            super.setNamePool(targetNamePool);
            return;
        }
        myConfig.setNamePool(targetNamePool);
    }

    @Override
    public void setModuleURIResolver(String className) throws TransformerException {
        if ( myConfig == null ) {
            super.setModuleURIResolver(className);
            return;
        }
        myConfig.setModuleURIResolver(className);
    }

    @Override
    public void setModuleURIResolver(ModuleURIResolver resolver) {
        if ( myConfig == null ) {
            super.setModuleURIResolver(resolver);
            return;
        }
        myConfig.setModuleURIResolver(resolver);
    }

    @Override
    public void setLocalizerFactory(LocalizerFactory factory) {
        if ( myConfig == null ) {
            super.setLocalizerFactory(factory);
            return;
        }
        myConfig.setLocalizerFactory(factory);
    }

    @Override
    public void setLineNumbering(boolean lineNumbering) {
        if ( myConfig == null ) {
            super.setLineNumbering(lineNumbering);
            return;
        }
        myConfig.setLineNumbering(lineNumbering);
    }

    @Override
    public void setExpandAttributeDefaults(boolean expand) {
        if ( myConfig == null ) {
            super.setExpandAttributeDefaults(expand);
            return;
        }
        myConfig.setExpandAttributeDefaults(expand);
    }

    @Override
    public void setErrorReporterFactory(final Function<Configuration, ? extends ErrorReporter> factory) {
        if ( myConfig == null ) {
            super.setErrorReporterFactory(factory);
            return;
        }
        myConfig.setErrorReporterFactory(factory);
    }

    @Override
    public void setDynamicLoader(IDynamicLoader dynamicLoader) {
        if ( myConfig == null ) {
            super.setDynamicLoader(dynamicLoader);
            return;
        }
        myConfig.setDynamicLoader(dynamicLoader);
    }

    @Override
    public void setDocumentNumberAllocator(DocumentNumberAllocator allocator) {
        if ( myConfig == null ) {
            super.setDocumentNumberAllocator(allocator);
            return;
        }
        myConfig.setDocumentNumberAllocator(allocator);
    }

    @Override
    public void setDefaultSerializationProperties(Properties props) {
        if ( myConfig == null ) {
            super.setDefaultSerializationProperties(props);
            return;
        }
        myConfig.setDefaultSerializationProperties(props);
    }

    @Override
    public void setDefaultLanguage(String language) {
        if ( myConfig == null ) {
            super.setDefaultLanguage(language);
            return;
        }
        myConfig.setDefaultLanguage(language);
    }

    @Override
    public void setDefaultCountry(String country) {
        if ( myConfig == null ) {
            super.setDefaultCountry(country);
            return;
        }
        myConfig.setDefaultCountry(country);
    }

    @Override
    public void setDefaultCollection(String uri) {
        if ( myConfig == null ) {
            super.setDefaultCollection(uri);
            return;
        }
        myConfig.setDefaultCollection(uri);
    }

    @Override
    public void setDebugger(Debugger debugger) {
        if ( myConfig == null ) {
            super.setDebugger(debugger);
            return;
        }
        myConfig.setDebugger(debugger);
    }

    @Override
    public void setConfigurationProperty(String name, Object value) {
        if ( myConfig == null ) {
            super.setConfigurationProperty(name, value);
            return;
        }
        myConfig.setConfigurationProperty(name, value);
    }

    @Override
    public void setCompileWithTracing(boolean trace) {
        if ( myConfig == null ) {
            super.setCompileWithTracing(trace);
            return;
        }
        myConfig.setCompileWithTracing(trace);
    }

    @Override
    public void setCollectionFinder(final CollectionFinder cf) {
        if ( myConfig == null ) {
            super.setCollectionFinder(cf);
            return;
        }
        myConfig.setCollectionFinder(cf);
    }

    @Override
    public void setCollationURIResolver(CollationURIResolver resolver) {
        if ( myConfig == null ) {
            super.setCollationURIResolver(resolver);
            return;
        }
        myConfig.setCollationURIResolver(resolver);
    }

    @Override
    public void sealNamespace(NamespaceUri namespace) {
        if ( myConfig == null ) {
            super.sealNamespace(namespace);
            return;
        }
        myConfig.sealNamespace(namespace);
    }

    @Override
    public synchronized void reuseSourceParser(XMLReader parser) {
        if ( myConfig == null ) {
            super.reuseSourceParser(parser);
            return;
        }
        myConfig.reuseSourceParser(parser);
    }

    @Override
    public ActiveSource resolveSource(Source source, Configuration config) throws XPathException {
        if ( myConfig == null ) {
            return super.resolveSource(source, config);
        }
        return myConfig.resolveSource(source, config);
    }

    @Override
    public void reportFatalError(XPathException err) {
        if ( myConfig == null ) {
            super.reportFatalError(err);
            return;
        }
        myConfig.reportFatalError(err);
    }

    @Override
    public void registerExternalObjectModel(ExternalObjectModel model) {
        if ( myConfig == null ) {
            super.registerExternalObjectModel(model);
            return;
        }
        myConfig.registerExternalObjectModel(model);
    }

    @Override
    public void registerExtensionFunction(ExtensionFunctionDefinition function) {
        if ( myConfig == null ) {
            super.registerExtensionFunction(function);
            return;
        }
        myConfig.registerExtensionFunction(function);
    }

    @Override
    public NamespaceUri readSchema(PipelineConfiguration pipe, String baseURI, String schemaLocation, NamespaceUri expected) throws SchemaException {
        if ( myConfig == null ) {
            return super.readSchema(pipe, baseURI, schemaLocation, expected);
        }
        return myConfig.readSchema(pipe, baseURI, schemaLocation, expected);
    }

    @Override
    public void readMultipleSchemas(PipelineConfiguration pipe, String baseURI, List<String> schemaLocations, NamespaceUri expected) throws SchemaException {
        if ( myConfig == null ) {
            super.readMultipleSchemas(pipe, baseURI, schemaLocations, expected);
            return;
        }
        myConfig.readMultipleSchemas(pipe, baseURI, schemaLocations, expected);
    }

    @Override
    public NamespaceUri readInlineSchema(NodeInfo root, NamespaceUri expected, ErrorReporter errorReporter) throws SchemaException {
        if ( myConfig == null ) {
            return super.readInlineSchema(root, expected, errorReporter);
        }
        return myConfig.readInlineSchema(root, expected, errorReporter);
    }

    @Override
    public UserFunction newUserFunction(boolean memoFunction, FunctionStreamability streamability) {
        if ( myConfig == null ) {
            return super.newUserFunction(memoFunction, streamability);
        }
        return myConfig.newUserFunction(memoFunction, streamability);
    }

    @Override
    public StaticQueryContext newStaticQueryContext() {
        if ( myConfig == null ) {
            return super.newStaticQueryContext();
        }
        return myConfig.newStaticQueryContext();
    }

    @Override
    public PendingUpdateList newPendingUpdateList() {
        if ( myConfig == null ) {
            return super.newPendingUpdateList();
        }
        return myConfig.newPendingUpdateList();
    }

    @Override
    public TraceListener makeTraceListener(String className) throws XPathException {
        if ( myConfig == null ) {
            return super.makeTraceListener(className);
        }
        return myConfig.makeTraceListener(className);
    }

    @Override
    public TraceListener makeTraceListener() throws XPathException {
        if ( myConfig == null ) {
            return super.makeTraceListener();
        }
        return myConfig.makeTraceListener();
    }

    @Override
    public Receiver makeStreamingTransformer(Mode mode, ParameterSet ordinaryParams, ParameterSet tunnelParams, Outputter output, XPathContext context) throws XPathException {
        if ( myConfig == null ) {
            return super.makeStreamingTransformer(mode, ordinaryParams, tunnelParams, output, context);
        }
        return myConfig.makeStreamingTransformer(mode, ordinaryParams, tunnelParams, output, context);
    }

    @Override
    public SlotManager makeSlotManager() {
        if ( myConfig == null ) {
            return super.makeSlotManager();
        }
        return myConfig.makeSlotManager();
    }

    @Override
    public PipelineConfiguration makePipelineConfiguration() {
        if ( myConfig == null ) {
            return super.makePipelineConfiguration();
        }
        return myConfig.makePipelineConfiguration();
    }

    @Override
    public XMLReader makeParser(String className) throws TransformerFactoryConfigurationError {
        if ( myConfig == null ) {
            return super.makeParser(className);
        }
        return myConfig.makeParser(className);
    }

    @Override
    public Numberer makeNumberer(String language, String country) {
        if ( myConfig == null ) {
            return super.makeNumberer(language, country);
        }
        return myConfig.makeNumberer(language, country);
    }

    @Override
    public Receiver makeEmitter(String clarkName, Properties props) throws XPathException {
        if ( myConfig == null ) {
            return super.makeEmitter(clarkName, props);
        }
        return myConfig.makeEmitter(clarkName, props);
    }

    @Override
    public FilterFactory makeDocumentProjector(PathMapRoot map) {
        if ( myConfig == null ) {
            return super.makeDocumentProjector(map);
        }
        return myConfig.makeDocumentProjector(map);
    }

    @Override
    public void loadSchema(String absoluteURI) throws SchemaException {
        if ( myConfig == null ) {
            super.loadSchema(absoluteURI);
            return;
        }
        myConfig.loadSchema(absoluteURI);
    }

    @Override
    public boolean isXIncludeAware() {
        if ( myConfig == null ) {
            return super.isXIncludeAware();
        }
        return myConfig.isXIncludeAware();
    }

    @Override
    public boolean isVersionWarning() {
        if ( myConfig == null ) {
            return super.isVersionWarning();
        }
        return myConfig.isVersionWarning();
    }

    @Override
    public boolean isValidationWarnings() {
        if ( myConfig == null ) {
            return super.isValidationWarnings();
        }
        return myConfig.isValidationWarnings();
    }

    @Override
    public boolean isValidation() {
        if ( myConfig == null ) {
            return super.isValidation();
        }
        return myConfig.isValidation();
    }

    @Override
    public boolean isTiming() {
        if ( myConfig == null ) {
            return super.isTiming();
        }
        return myConfig.isTiming();
    }

    @Override
    public boolean isStripsAllWhiteSpace() {
        if ( myConfig == null ) {
            return super.isStripsAllWhiteSpace();
        }
        return myConfig.isStripsAllWhiteSpace();
    }

    @Override
    public boolean isSchemaAvailable(NamespaceUri targetNamespace) {
        if ( myConfig == null ) {
            return super.isSchemaAvailable(targetNamespace);
        }
        return myConfig.isSchemaAvailable(targetNamespace);
    }

    @Override
    public boolean isLineNumbering() {
        if ( myConfig == null ) {
            return super.isLineNumbering();
        }
        return myConfig.isLineNumbering();
    }

    @Override
    public boolean isLicensedFeature(int feature) {
        if ( myConfig == null ) {
            return super.isLicensedFeature(feature);
        }
        return myConfig.isLicensedFeature(feature);
    }

    @Override
    public boolean isExpandAttributeDefaults() {
        if ( myConfig == null ) {
            return super.isExpandAttributeDefaults();
        }
        return myConfig.isExpandAttributeDefaults();
    }

    @Override
    public boolean isCompileWithTracing() {
        if ( myConfig == null ) {
            return super.isCompileWithTracing();
        }
        return myConfig.isCompileWithTracing();
    }

    @Override
    public boolean isCompatible(Configuration other) {
        if ( myConfig == null ) {
            return super.isCompatible(other);
        }
        return myConfig.isCompatible(other);
    }

    @Override
    public void importComponents(Source source) throws XPathException {
        if ( myConfig == null ) {
            super.importComponents(source);
            return;
        }
        myConfig.importComponents(source);
    }

    @Override
    public int getXsdVersion() {
        if ( myConfig == null ) {
            return super.getXsdVersion();
        }
        return myConfig.getXsdVersion();
    }

    @Override
    public int getXMLVersion() {
        if ( myConfig == null ) {
            return super.getXMLVersion();
        }
        return myConfig.getXMLVersion();
    }

    @Override
    public int getTreeModel() {
        if ( myConfig == null ) {
            return super.getTreeModel();
        }
        return myConfig.getTreeModel();
    }

    @Override
    public String getTraceListenerClass() {
        if ( myConfig == null ) {
            return super.getTraceListenerClass();
        }
        return myConfig.getTraceListenerClass();
    }

    @Override
    public TraceListener getTraceListener() {
        if ( myConfig == null ) {
            return super.getTraceListener();
        }
        return myConfig.getTraceListener();
    }

    @Override
    public ModuleURIResolver getStandardModuleURIResolver() {
        if ( myConfig == null ) {
            return super.getStandardModuleURIResolver();
        }
        return myConfig.getStandardModuleURIResolver();
    }

    @Override
    public SourceResolver getSourceResolver() {
        if ( myConfig == null ) {
            return super.getSourceResolver();
        }
        return myConfig.getSourceResolver();
    }

    @Override
    public String getSourceParserClass() {
        if ( myConfig == null ) {
            return super.getSourceParserClass();
        }
        return myConfig.getSourceParserClass();
    }

    @Override
    public synchronized XMLReader getSourceParser() throws TransformerFactoryConfigurationError {
        if ( myConfig == null ) {
            return super.getSourceParser();
        }
        return myConfig.getSourceParser();
    }

    @Override
    public SerializerFactory getSerializerFactory() {
        if ( myConfig == null ) {
            return super.getSerializerFactory();
        }
        return myConfig.getSerializerFactory();
    }

    @Override
    public int getSchemaValidationMode() {
        if ( myConfig == null ) {
            return super.getSchemaValidationMode();
        }
        return myConfig.getSchemaValidationMode();
    }

    @Override
    public SchemaType getSchemaType(StructuredQName name) {
        if ( myConfig == null ) {
            return super.getSchemaType(name);
        }
        return myConfig.getSchemaType(name);
    }

    @Override
    public String getProductTitle() {
        if ( myConfig == null ) {
            return super.getProductTitle();
        }
        return myConfig.getProductTitle();
    }

    @Override
    public ApiProvider getProcessor() {
        if ( myConfig == null ) {
            return super.getProcessor();
        }
        return myConfig.getProcessor();
    }

    @Override
    public ParseOptions getParseOptions() {
        if ( myConfig == null ) {
            return super.getParseOptions();
        }
        return myConfig.getParseOptions();
    }

    @Override
    public OutputURIResolver getOutputURIResolver() {
        if ( myConfig == null ) {
            return super.getOutputURIResolver();
        }
        return myConfig.getOutputURIResolver();
    }

    @Override
    public NamePool getNamePool() {
        if ( myConfig == null ) {
            return super.getNamePool();
        }
        return myConfig.getNamePool();
    }

    @Override
    public ModuleURIResolver getModuleURIResolver() {
        if ( myConfig == null ) {
            return super.getModuleURIResolver();
        }
        return myConfig.getModuleURIResolver();
    }

    @Override
    public LocalizerFactory getLocalizerFactory() {
        if ( myConfig == null ) {
            return super.getLocalizerFactory();
        }
        return myConfig.getLocalizerFactory();
    }

    @Override
    public IntegratedFunctionLibrary getIntegratedFunctionLibrary() {
        if ( myConfig == null ) {
            return super.getIntegratedFunctionLibrary();
        }
        return myConfig.getIntegratedFunctionLibrary();
    }

    @Override
    public Object getInstance(String className) throws XPathException {
        if ( myConfig == null ) {
            return super.getInstance(className);
        }
        return myConfig.getInstance(className);
    }

    @Override
    public Set getImportedNamespaces() {
        if ( myConfig == null ) {
            return super.getImportedNamespaces();
        }
        return myConfig.getImportedNamespaces();
    }

    @Override
    public DocumentPool getGlobalDocumentPool() {
        if ( myConfig == null ) {
            return super.getGlobalDocumentPool();
        }
        return myConfig.getGlobalDocumentPool();
    }

    @Override
    public List<ExternalObjectModel> getExternalObjectModels() {
        if ( myConfig == null ) {
            return super.getExternalObjectModels();
        }
        return myConfig.getExternalObjectModels();
    }

    @Override
    public ExternalObjectModel getExternalObjectModel(Class nodeClass) {
        if ( myConfig == null ) {
            return super.getExternalObjectModel(nodeClass);
        }
        return myConfig.getExternalObjectModel(nodeClass);
    }

    @Override
    public ExternalObjectModel getExternalObjectModel(String uri) {
        if ( myConfig == null ) {
            return super.getExternalObjectModel(uri);
        }
        return myConfig.getExternalObjectModel(uri);
    }

    @Override
    public Iterable<? extends SchemaType> getExtensionsOfType(SchemaType type) {
        if ( myConfig == null ) {
            return super.getExtensionsOfType(type);
        }
        return myConfig.getExtensionsOfType(type);
    }

    @Override
    public Receiver getElementValidator(Receiver receiver, ParseOptions validationOptions, Location locationId) throws XPathException {
        if ( myConfig == null ) {
            return super.getElementValidator(receiver, validationOptions, locationId);
        }
        return myConfig.getElementValidator(receiver, validationOptions, locationId);
    }

    @Override
    public SchemaDeclaration getElementDeclaration(int fingerprint) {
        if ( myConfig == null ) {
            return super.getElementDeclaration(fingerprint);
        }
        return myConfig.getElementDeclaration(fingerprint);
    }

    @Override
    public String getEditionCode() {
        if ( myConfig == null ) {
            return super.getEditionCode();
        }
        return myConfig.getEditionCode();
    }

    @Override
    public IDynamicLoader getDynamicLoader() {
        if ( myConfig == null ) {
            return super.getDynamicLoader();
        }
        return myConfig.getDynamicLoader();
    }

    @Override
    public Receiver getDocumentValidator(Receiver receiver, String systemId, ParseOptions validationOptions, Location initiatingLocation) {
        if ( myConfig == null ) {
            return super.getDocumentValidator(receiver, systemId, validationOptions, initiatingLocation);
        }
        return myConfig.getDocumentValidator(receiver, systemId, validationOptions, initiatingLocation);
    }

    @Override
    public DocumentNumberAllocator getDocumentNumberAllocator() {
        if ( myConfig == null ) {
            return super.getDocumentNumberAllocator();
        }
        return myConfig.getDocumentNumberAllocator();
    }

    @Override
    public CompilerInfo getDefaultXsltCompilerInfo() {
        if ( myConfig == null ) {
            return super.getDefaultXsltCompilerInfo();
        }
        return myConfig.getDefaultXsltCompilerInfo();
    }

    @Override
    public StaticQueryContext getDefaultStaticQueryContext() {
        if ( myConfig == null ) {
            return super.getDefaultStaticQueryContext();
        }
        return myConfig.getDefaultStaticQueryContext();
    }

    @Override
    public Properties getDefaultSerializationProperties() {
        if ( myConfig == null ) {
            return super.getDefaultSerializationProperties();
        }
        return myConfig.getDefaultSerializationProperties();
    }

    @Override
    public String getDefaultLanguage() {
        if ( myConfig == null ) {
            return super.getDefaultLanguage();
        }
        return myConfig.getDefaultLanguage();
    }

    @Override
    public String getDefaultCountry() {
        if ( myConfig == null ) {
            return super.getDefaultCountry();
        }
        return myConfig.getDefaultCountry();
    }

    @Override
    public String getDefaultCollection() {
        if ( myConfig == null ) {
            return super.getDefaultCollection();
        }
        return myConfig.getDefaultCollection();
    }

    @Override
    public Debugger getDebugger() {
        if ( myConfig == null ) {
            return super.getDebugger();
        }
        return myConfig.getDebugger();
    }

    @Override
    public XPathContext getConversionContext() {
        if ( myConfig == null ) {
            return super.getConversionContext();
        }
        return myConfig.getConversionContext();
    }

    @Override
    public Object getConfigurationProperty(String name) {
        if ( myConfig == null ) {
            return super.getConfigurationProperty(name);
        }
        return myConfig.getConfigurationProperty(name);
    }

    @Override
    public CollectionFinder getCollectionFinder() {
        if ( myConfig == null ) {
            return super.getCollectionFinder();
        }
        return myConfig.getCollectionFinder();
    }

    @Override
    public CollationURIResolver getCollationURIResolver() {
        if ( myConfig == null ) {
            return super.getCollationURIResolver();
        }
        return myConfig.getCollationURIResolver();
    }

    @Override
    public Class getClass(String className, boolean tracing) throws XPathException {
        if ( myConfig == null ) {
            return super.getClass();
        }
        return myConfig.getClass(className, tracing);
    }

    @Override
    public CharacterSetFactory getCharacterSetFactory() {
        if ( myConfig == null ) {
            return super.getCharacterSetFactory();
        }
        return myConfig.getCharacterSetFactory();
    }

    @Override
    public SchemaDeclaration getAttributeDeclaration(int fingerprint) {
        if ( myConfig == null ) {
            return super.getAttributeDeclaration(fingerprint);
        }
        return myConfig.getAttributeDeclaration(fingerprint);
    }

    @Override
    public Receiver getAnnotationStripper(Receiver destination) {
        if ( myConfig == null ) {
            return super.getAnnotationStripper(destination);
        }
        return myConfig.getAnnotationStripper(destination);
    }

    @Override
    public void exportComponents(Receiver out) throws XPathException {
        if ( myConfig == null ) {
            super.exportComponents(out);
            return;
        }
        myConfig.exportComponents(out);
    }

    @Override
    public void displayLicenseMessage() {
        if ( myConfig == null ) {
            super.displayLicenseMessage();
            return;
        }
        myConfig.displayLicenseMessage();
    }

    @Override
    public void checkTypeDerivationIsOK(SchemaType derived, SchemaType base, int block) throws SchemaException {
        if ( myConfig == null ) {
            super.checkTypeDerivationIsOK(derived, base, block);
            return;
        }
        myConfig.checkTypeDerivationIsOK(derived, base, block);
    }

    @Override
    public TreeInfo buildDocumentTree(Source source) throws XPathException {
        if ( myConfig == null ) {
            return super.buildDocumentTree(source);
        }
        return myConfig.buildDocumentTree(source);
    }

    @Override
    public TreeInfo buildDocumentTree(Source source, ParseOptions parseOptions) throws XPathException {
        if ( myConfig == null ) {
            return super.buildDocumentTree(source, parseOptions);
        }
        return myConfig.buildDocumentTree(source, parseOptions);
    }

    @Override
    public void addSchemaSource(final Source schemaSource, final ErrorReporter errorReporter) throws SchemaException {
        if ( myConfig == null ) {
            super.addSchemaSource(schemaSource, errorReporter);
            return;
        }
        myConfig.addSchemaSource(schemaSource, errorReporter);
    }

    @Override
    public void addSchemaSource(Source schemaSource) throws SchemaException {
        if ( myConfig == null ) {
            super.addSchemaSource(schemaSource);
            return;
        }
        myConfig.addSchemaSource(schemaSource);
    }

    @Override
    public void addExtensionBinders(FunctionLibraryList list) {
        if ( myConfig == null ) {
            super.addExtensionBinders(list);
            return;
        }
        myConfig.addExtensionBinders(list);
    }

    private final Configuration myConfig;
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
