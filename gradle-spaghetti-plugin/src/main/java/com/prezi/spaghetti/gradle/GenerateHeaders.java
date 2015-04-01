package com.prezi.spaghetti.gradle;

import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.generator.Generators;
import com.prezi.spaghetti.generator.HeaderGenerator;
import com.prezi.spaghetti.generator.internal.DefaultGeneratorParameters;
import com.prezi.spaghetti.generator.internal.InternalGeneratorUtils;
import com.prezi.spaghetti.gradle.internal.AbstractDefinitionAwareSpaghettiTask;
import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

public class GenerateHeaders extends AbstractDefinitionAwareSpaghettiTask {
	private File outputDirectory;

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
		DefaultGeneratorParameters generatorParams = new DefaultGeneratorParameters(config, InternalGeneratorUtils.createHeader(), getOptions());
		generator.generateHeaders(generatorParams, directory);
	}
}
