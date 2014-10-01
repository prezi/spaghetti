package com.prezi.spaghetti.cli.commands;

import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.definition.ModuleConfigurationParser;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import io.airlift.command.Option;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public abstract class AbstractDefinitionAwareCommand extends AbstractSpaghettiCommand {
	@Option(name = {"-m", "--definition"},
			description = "The module definition",
			required = true)
	protected File definition;

	protected ModuleConfiguration parseConfig() throws IOException {
		ModuleDefinitionSource localSource  = parseDefinition(definition);
		Collection<ModuleDefinitionSource> dependentSources = parseDefinitionSources(dependencyPath);
		return ModuleConfigurationParser.parse(localSource, dependentSources);
	}

	private static Collection<ModuleDefinitionSource> parseDefinitionSources(String path) throws IOException {
		Collection<ModuleDefinitionSource> sources = Sets.newLinkedHashSet();
		for (ModuleBundle bundle : parseBundles(path)) {
			sources.add(ModuleDefinitionSource.fromBundle(bundle));
		}
		return sources;
	}

	private static ModuleDefinitionSource parseDefinition(File file) throws IOException {
		return ModuleDefinitionSource.fromFile(file);
	}
}
