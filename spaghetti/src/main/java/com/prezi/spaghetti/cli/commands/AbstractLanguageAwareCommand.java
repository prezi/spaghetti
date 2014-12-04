package com.prezi.spaghetti.cli.commands;

import io.airlift.command.Option;

public abstract class AbstractLanguageAwareCommand extends AbstractDefinitionAwareCommand {

	@Option(name = {"-l", "--language"},
			description = "Implementation language",
			required = true)
	protected String language;
}
