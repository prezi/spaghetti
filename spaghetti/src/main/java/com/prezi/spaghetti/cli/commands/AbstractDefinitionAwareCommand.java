package com.prezi.spaghetti.cli.commands;

import com.prezi.spaghetti.bundle.ModuleBundleSet;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.definition.internal.ModuleConfigurationParser;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import com.prezi.spaghetti.definition.internal.DefaultModuleDefinitionSource;
import io.airlift.command.Option;

import java.io.File;
import java.io.IOException;

public abstract class AbstractDefinitionAwareCommand extends AbstractSpaghettiCommand {
	@Option(name = {"-m", "--definition"},
			description = "The module definition",
			required = true)
	protected File definition;

	protected ModuleConfiguration parseConfig() throws IOException {
		ModuleDefinitionSource localSource  = parseDefinition(definition);
		ModuleBundleSet bundles = lookupBundles();
		return ModuleConfigurationParser.parse(localSource, bundles);
	}

	private static ModuleDefinitionSource parseDefinition(File file) throws IOException {
		return DefaultModuleDefinitionSource.fromFile(file);
	}
}
