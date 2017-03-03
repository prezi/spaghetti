package com.prezi.spaghetti.gradle.internal;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleFormat;
import com.prezi.spaghetti.bundle.ModuleBundleFactory;
import com.prezi.spaghetti.bundle.internal.BundleUtils;
import com.prezi.spaghetti.bundle.internal.ModuleBundleParameters;
import com.prezi.spaghetti.definition.EntityWithModuleMetaData;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.generator.JavaScriptBundleProcessor;
import com.prezi.spaghetti.generator.internal.DefaultJavaScriptBundleProcessorParameters;
import com.prezi.spaghetti.generator.internal.Generators;
import com.prezi.spaghetti.generator.internal.InternalGeneratorUtils;
import com.prezi.spaghetti.packaging.internal.ExternalDependencyGenerator;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

public class AbstractBundleModuleTask extends AbstractDefinitionAwareSpaghettiTask {
	private File inputFile;
	private File outputDirectory;
	private String sourceBaseUrl;
	private File sourceMap;
	private File definitionOverride;
	private File resourcesDirectoryInternal;
	private final ConfigurableFileCollection prefixes = getProject().files();
	private final ConfigurableFileCollection suffixes = getProject().files();
	private Map<String, String> externalDependencies = Maps.newTreeMap();

	@InputFile
	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(Object inputFile) {
		this.inputFile = getProject().file(inputFile);
	}

	public void inputFile(Object inputFile) {
		setInputFile(inputFile);
	}

	@OutputDirectory
	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(Object outputDirectory) {
		this.outputDirectory = getProject().file(outputDirectory);
	}

	public void outputDirectory(Object outputDirectory) {
		setOutputDirectory(outputDirectory);
	}

	@Input
	@Optional
	public String getSourceBaseUrl() {
		return sourceBaseUrl;
	}

	public void setSourceBaseUrl(String sourceBaseUrl) {
		this.sourceBaseUrl = sourceBaseUrl;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void sourceBaseUrl(String source) {
		setSourceBaseUrl(source);
	}

	@InputFile
	@Optional
	public File getSourceMap() {
		if (sourceMap == null) {
			// This should probably be done with convention mapping
			File defSourceMap = new File(getInputFile().getParentFile(), getInputFile().getName() + ".map");
			if (defSourceMap.exists()) {
				sourceMap = defSourceMap;
			}
		}
		return sourceMap;
	}

	public void setSourceMap(Object sourceMap) {
		this.sourceMap = getProject().file(sourceMap);
	}

	@SuppressWarnings("UnusedDeclaration")
	public void sourceMap(Object sourceMap) {
		setSourceMap(sourceMap);
	}

	public void setDefinitionOverride(File definitionOverride) {
		this.definitionOverride = definitionOverride;
	}

	public File getDefinitionOverride() {
		return definitionOverride;
	}

	protected File getOriginalDefinitionOrOverride() {
		File def = getDefinitionOverride();
		return def == null ? getDefinition() : def;
	}

	protected File getResourcesDirectoryInternal() {
		return resourcesDirectoryInternal;
	}

	protected void setResourcesDirectoryInternal(Object resourcesDirectoryInternal) {
		this.resourcesDirectoryInternal = getProject().file(resourcesDirectoryInternal);
	}

	@SuppressWarnings("UnusedDeclaration")
	public void setResourcesDirectory(Object resourcesDir) {
		setResourcesDirectoryInternal(resourcesDir);
	}

	@SuppressWarnings("UnusedDeclaration")
	public void resourcesDirectory(Object resourcesDir) {
		setResourcesDirectoryInternal(resourcesDir);
	}

	@InputDirectory
	@Optional
	public File getResourcesDirectory() {
		File dir = getResourcesDirectoryInternal();
		return (dir != null && dir.exists()) ? dir : null;
	}

	public AbstractBundleModuleTask() {
		this.getConventionMapping().map("inputFile", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return new File(getProject().getBuildDir(), "module.js");
			}

		});
	}

	@InputFiles
	public ConfigurableFileCollection getPrefixes() {
		return prefixes;
	}

	public void prefixes(Object... prefixes) {
		this.getPrefixes().from(prefixes);
	}

	public void prefix(Object... prefixes) {
		this.prefixes(prefixes);
	}

	@InputFiles
	public ConfigurableFileCollection getSuffixes() {
		return suffixes;
	}

	public void suffixes(Object... suffixes) {
		this.getSuffixes().from(suffixes);
	}

	public void suffix(Object... suffixes) {
		this.suffixes(suffixes);
	}

	@Input
	public Map<String, String> getExternalDependencies() {
		return externalDependencies;
	}

	public void setExternalDependencies(Map<String, String> externalDependencies) {
		this.externalDependencies = externalDependencies;
	}
	public void externalDependencies(Map<String, String> externalDependencies) {
		this.externalDependencies.putAll(externalDependencies);
	}
	public void externalDependencies(Set<String> externalDependencies) {
		externalDependencies(BundleUtils.parseExternalDependencies(externalDependencies));
	}
	public void externalDependency(String importName, String dependencyName) {
		externalDependencies(ImmutableSortedMap.of(importName, dependencyName));
	}
	public void externalDependency(String shorthand) {
		externalDependency(shorthand, shorthand);
	}

	@TaskAction
	public final ModuleBundle bundle() throws IOException {
		ModuleConfiguration config = readConfig(getOriginalDefinitionOrOverride());

		String inputContents = "";
		for (File prefixFile : getPrefixes()) {
			inputContents += Files.asCharSource(prefixFile, Charsets.UTF_8).read();
		}
		inputContents += Files.asCharSource(getInputFile(), Charsets.UTF_8).read();
		for (File suffixFile : getSuffixes()) {
			inputContents += Files.asCharSource(suffixFile, Charsets.UTF_8).read();
		}

		JavaScriptBundleProcessor javaScriptBundleProcessor = Generators.getService(JavaScriptBundleProcessor.class, getLanguage());
		DefaultJavaScriptBundleProcessorParameters processorParams = new DefaultJavaScriptBundleProcessorParameters(config);
		List<String> importedExternalDependencyVars = ExternalDependencyGenerator.getImportedVarNames(externalDependencies.keySet());
		String processedJavaScript = InternalGeneratorUtils.bundleJavaScript(javaScriptBundleProcessor.processModuleJavaScript(processorParams, inputContents), importedExternalDependencyVars);

		File sourceMap = getSourceMap();
		String sourceMapText = sourceMap != null ? Files.asCharSource(sourceMap, Charsets.UTF_8).read() : null;

		return createBundle(config, processedJavaScript, sourceMapText, getResourcesDirectory());
	}

	protected ModuleBundle createBundle(ModuleConfiguration config, String javaScript, String sourceMap, File resourceDir) throws IOException {
		ModuleNode module = config.getLocalModule();
		File outputDir = getOutputDirectory();
		getLogger().info("Creating bundle in {}", outputDir);
		TreeSet<String> dependentModuleNames = Sets.newTreeSet();
		for (EntityWithModuleMetaData<ModuleNode> moduleNode : config.getDirectDependentModules()) {
			dependentModuleNames.add(moduleNode.getEntity().getName());
		}

		return ModuleBundleFactory.createDirectory(getOutputDirectory(), new ModuleBundleParameters(
				module.getName(),
				module.getSource().getContents(),
				module.getSource().getDefinitionLanguage(),
				String.valueOf(getProject().getVersion()),
				ModuleFormat.UMD,
				getSourceBaseUrl(),
				javaScript,
				sourceMap,
				dependentModuleNames,
				externalDependencies,
				resourceDir
		));
	}
}
