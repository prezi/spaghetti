package com.prezi.spaghetti.cli.commands;

import com.prezi.spaghetti.cli.SpaghettiCliException;
import com.prezi.spaghetti.config.ModuleConfiguration;
import com.prezi.spaghetti.generator.Generator;
import io.airlift.command.Command;
import io.airlift.command.Option;
import org.apache.commons.io.FileUtils;

import java.io.File;

@Command(name = "headers", description = "Generates headers in the implementation language")
public class GenerateHeadersCommand extends AbstractLanguageAwareCommand {

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
		Generator generator = createGenerator(config);
		FileUtils.deleteDirectory(outputDirectory);
		FileUtils.forceMkdir(outputDirectory);
		generator.generateHeaders(outputDirectory);
		return 0;
	}
}
