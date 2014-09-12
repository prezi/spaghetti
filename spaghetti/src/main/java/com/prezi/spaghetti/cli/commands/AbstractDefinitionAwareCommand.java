package com.prezi.spaghetti.cli.commands;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.config.ModuleConfiguration;
import com.prezi.spaghetti.config.ModuleConfigurationParser;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import io.airlift.command.Option;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public abstract class AbstractDefinitionAwareCommand extends AbstractSpaghettiCommand {
	@Option(name = {"-m", "--definition"},
			description = "The module definition",
			required = true)
	protected File definition;

	@Option(name = {"-t", "--transitive-dependency-path"},
			description = "List of transitively dependent module bundles separated by colon (':')")
	protected String transitiveDependencyPath;

	protected ModuleConfiguration parseConfig() throws IOException {
		Collection<ModuleDefinitionSource> localSources  = Collections.singleton(parseDefinition(definition));
		Collection<ModuleDefinitionSource> dependentSources = parseDefinitionSources(directDependencyPath);
		Collection<ModuleDefinitionSource> transitiveSources = parseDefinitionSources(transitiveDependencyPath);
		return ModuleConfigurationParser.parse(localSources, dependentSources, transitiveSources);
	}

	private static Collection<ModuleDefinitionSource> parseDefinitionSources(String path) throws IOException {
		Collection<ModuleDefinitionSource> sources = Sets.newLinkedHashSet();
		for (ModuleBundle bundle : parseBundles(path)) {
			sources.add(new ModuleDefinitionSource(bundle.getName(), bundle.getDefinition()));
		}
		return sources;
	}

	private static ModuleDefinitionSource parseDefinition(File file) throws IOException {
		String contents = Files.asCharSource(file, Charsets.UTF_8).read();
		return new ModuleDefinitionSource(file.getPath(), contents);
	}
}
