package com.prezi.spaghetti.gradle;

import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.internal.DependentFiles;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.definition.ModuleConfigurationParser;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import com.prezi.spaghetti.generator.Generators;
import com.prezi.spaghetti.generator.HeaderGenerator;
import com.prezi.spaghetti.generator.internal.DefaultGeneratorParameters;
import com.prezi.spaghetti.generator.internal.InternalGeneratorUtils;
import com.prezi.spaghetti.gradle.internal.AbstractDefinitionAwareSpaghettiTask;
import com.prezi.spaghetti.gradle.internal.ModuleBundleLookup;
import com.prezi.spaghetti.gradle.internal.SpaghettiExtension;
import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class GenerateHeaders extends AbstractDefinitionAwareSpaghettiTask {
	private File outputDirectory;

	public GenerateHeaders() {
		SpaghettiExtension extension = getProject().getExtensions().getByType(SpaghettiExtension.class);
		setDependentModules(getProject().files(extension.getModuleDefinitionConfiguration()));
	}

	@OutputDirectory
	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(Object outputDirectory) {
		this.outputDirectory = getProject().file(outputDirectory);
	}

	public void outputDirectory(Object directory) {
		setOutputDirectory(directory);
	}

	@TaskAction
	public void generate() throws IOException {

		ModuleConfiguration config = readConfig(getDefinition());
		getLogger().info("Generating module headers for {}", config.getLocalModule());
		File directory = getOutputDirectory();
		FileUtils.deleteQuietly(directory);
		FileUtils.forceMkdir(directory);
		HeaderGenerator generator = Generators.getService(HeaderGenerator.class, getLanguage());
		DefaultGeneratorParameters generatorParams = new DefaultGeneratorParameters(config, InternalGeneratorUtils.createHeader());
		generator.generateHeaders(generatorParams, directory);
	}

	@Input
	protected SortedSet<String> getDirectDependentBundleNames() throws IOException {
		TreeSet directBundleNames = Sets.newTreeSet();
		Iterator var2 = ModuleBundleLookup.lookupDependenciesAsFiles(getProject(), getDependentModules()).getDirectFiles().iterator();

		while(var2.hasNext()) {
			File bundle = (File)var2.next();
			directBundleNames.add(bundle.getName());
		}

		return directBundleNames;
	}

	public ModuleConfiguration readConfig(File definition) throws IOException {
		ModuleDefinitionSource definitionSource;
		try {
			definitionSource = ModuleDefinitionSource.fromFile(definition);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return readConfigInternal(definitionSource);
	}

	private ModuleConfiguration readConfigInternal(ModuleDefinitionSource localDefinition) throws IOException {
		DependentFiles dependentFiles = ModuleBundleLookup.lookupDependenciesAsFiles(getProject(), getDependentModules());
		ModuleConfiguration config = ModuleConfigurationParser.parse(localDefinition, dependentFiles);
		getLogger().info("Loaded configuration: {}", config);
		return config;
	}
}
