package com.prezi.spaghetti.cli.commands;

import com.prezi.spaghetti.cli.SpaghettiCliException;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.generator.Generators;
import com.prezi.spaghetti.generator.StubGenerator;
import com.prezi.spaghetti.generator.internal.DefaultGeneratorParameters;
import com.prezi.spaghetti.generator.internal.InternalGeneratorUtils;
import io.airlift.command.Command;
import io.airlift.command.Option;
import org.apache.commons.io.FileUtils;

import java.io.File;

@Command(name = "stubs", description = "Generates stubs in the implementation language")
public class GenerateStubsCommand extends AbstractLanguageAwareCommand {

	@Option(name = {"-o", "--output", "--output-directory"},
			description = "Output directory",
			required = true)
	private File outputDirectory;

	@Option(name = {"-f", "--force"},
			description = "Overwrite output directory if exists")
	private boolean force;

	@Override
	public Integer call() throws Exception {
		if (!force && outputDirectory.exists()) {
			throw new SpaghettiCliException("Output directory exists: " + outputDirectory);
		}

		ModuleConfiguration config = parseConfig();
		FileUtils.deleteDirectory(outputDirectory);
		FileUtils.forceMkdir(outputDirectory);
		StubGenerator generator = Generators.getService(StubGenerator.class, language);
		DefaultGeneratorParameters generatorParams = new DefaultGeneratorParameters(config, InternalGeneratorUtils.createHeader(), getParsedOptions());
		generator.generateStubs(generatorParams, outputDirectory);
		return 0;
	}
}
