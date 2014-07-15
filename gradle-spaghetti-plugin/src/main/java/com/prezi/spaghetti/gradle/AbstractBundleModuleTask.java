package com.prezi.spaghetti.gradle;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleFactory;
import com.prezi.spaghetti.bundle.ModuleBundleParameters;
import com.prezi.spaghetti.config.ModuleConfiguration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;
import java.util.concurrent.Callable;

public class AbstractBundleModuleTask extends AbstractDefinitionAwareSpaghettiTask {
	private File inputFile;
	private File outputDirectory;
	private String sourceBaseUrl;
	private File sourceMap;
	private File resourcesDirectoryInternal;

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
		return dir.exists() ? dir : null;
	}

	public AbstractBundleModuleTask() {
		this.getConventionMapping().map("inputFile", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return new File(getProject().getBuildDir(), "module.js");
			}

		});
	}

	@TaskAction
	public final ModuleBundle bundle() throws IOException {
		final FileCollection moduleDefinitions = getDefinitions();
		if (moduleDefinitions.isEmpty()) {
			throw new IllegalArgumentException("No module definition present");
		}

		if (moduleDefinitions.getFiles().size() > 1) {
			throw new IllegalArgumentException("Too many module definitions present: " + String.valueOf(moduleDefinitions));
		}

		ModuleConfiguration config = readConfig(moduleDefinitions);
		ModuleNode module = config.getLocalModules().iterator().next();
		String inputContents = Files.asCharSource(getInputFile(), Charsets.UTF_8).read();
		String processedJavaScript = createGenerator(config).processModuleJavaScript(module, inputContents);

		File sourceMap = getSourceMap();
		String sourceMapText = sourceMap != null ? Files.asCharSource(sourceMap, Charsets.UTF_8).read() : null;

		return createBundle(config, module, processedJavaScript, sourceMapText, getResourcesDirectory());
	}

	protected ModuleBundle createBundle(ModuleConfiguration config, ModuleNode module, String javaScript, String sourceMap, File resourceDir) throws IOException {
		File outputDir = getOutputDirectory();
		getLogger().info("Creating bundle in {}", outputDir);
		TreeSet<String> directDependentModuleNames = Sets.newTreeSet();
		for (ModuleNode moduleNode : config.getDirectDependentModules()) {
			directDependentModuleNames.add(moduleNode.getName());
		}

		return ModuleBundleFactory.createDirectory(getOutputDirectory(), new ModuleBundleParameters(
				module.getName(),
				module.getSource().getContents(),
				String.valueOf(getProject().getVersion()),
				getSourceBaseUrl(),
				javaScript,
				sourceMap,
				directDependentModuleNames,
				resourceDir
		));
	}
}
