package com.prezi.spaghetti.cli.commands;

import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.generator.Generator;
import com.prezi.spaghetti.generator.Languages;
import io.airlift.command.Option;

import java.io.IOException;

public abstract class AbstractLanguageAwareCommand extends AbstractDefinitionAwareCommand {

	@Option(name = {"-l", "--language"},
			description = "Implementation language",
			required = true)
	protected String language;

	protected Generator createGenerator(ModuleConfiguration config) throws IOException {
		return Languages.createGeneratorForLanguage(language, config);
	}
}
