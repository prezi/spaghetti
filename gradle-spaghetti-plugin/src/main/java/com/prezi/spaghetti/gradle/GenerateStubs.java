package com.prezi.spaghetti.gradle;

import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.generator.Generators;
import com.prezi.spaghetti.generator.StubGenerator;
import com.prezi.spaghetti.generator.internal.DefaultGeneratorParameters;
import com.prezi.spaghetti.generator.internal.InternalGeneratorUtils;
import com.prezi.spaghetti.gradle.internal.AbstractDefinitionAwareSpaghettiTask;
import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class GenerateStubs extends AbstractDefinitionAwareSpaghettiTask {
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

	public GenerateStubs() {
		this.getConventionMapping().map("outputDirectory", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return new File(getProject().getBuildDir(), "spaghetti/generated-stubs");
			}

		});
	}

	@TaskAction
	public void generate() throws IOException {
		ModuleConfiguration config = readConfig(getDefinition());
		getLogger().info("Generating module stubs for {}", config.getAllModules());
		File directory = getOutputDirectory();
		FileUtils.deleteQuietly(directory);
		FileUtils.forceMkdir(directory);
		StubGenerator generator = Generators.getService(StubGenerator.class, getLanguage());
		DefaultGeneratorParameters generatorParams = new DefaultGeneratorParameters(config, InternalGeneratorUtils.createHeader());
		generator.generateStubs(generatorParams, directory);
	}
}
