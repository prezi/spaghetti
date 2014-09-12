package com.prezi.spaghetti.cli.commands;

import com.prezi.spaghetti.Generator;
import com.prezi.spaghetti.Platforms;
import com.prezi.spaghetti.config.ModuleConfiguration;
import io.airlift.command.Option;

import java.io.IOException;

public abstract class AbstractLanguageAwareCommand extends AbstractDefinitionAwareCommand {

	@Option(name = {"-l", "--language"},
			description = "Implementation language",
			required = true)
	protected String language;

	protected Generator createGenerator(ModuleConfiguration config) throws IOException {
		return Platforms.createGeneratorForPlatform(language, config);
	}
}
