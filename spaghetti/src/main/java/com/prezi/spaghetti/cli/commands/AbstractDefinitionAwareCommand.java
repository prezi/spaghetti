package com.prezi.spaghetti.cli.commands;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleFactory;
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

	protected ModuleConfiguration parseConfig() throws IOException {
		Collection<ModuleDefinitionSource> localSources  = Collections.singleton(parseDefinition(definition));
		Collection<ModuleDefinitionSource> dependentSources = parseSourceBundles(directDependencyPath);
		Collection<ModuleDefinitionSource> transitiveSources = parseSourceBundles(transitiveDependencyPath);
		return ModuleConfigurationParser.parse(localSources, dependentSources, transitiveSources);
	}

	private static Collection<ModuleDefinitionSource> parseSourceBundles(String path) throws IOException {
		Collection<ModuleDefinitionSource> sources = Sets.newLinkedHashSet();
		if (!Strings.isNullOrEmpty(path)) {
			for (String bundlePath : path.split(":")) {
				File bundleFile = new File(bundlePath);
				ModuleBundle bundle = ModuleBundleFactory.load(bundleFile);
				sources.add(new ModuleDefinitionSource(bundleFile.getPath(), bundle.getDefinition()));
			}
		}
		return sources;
	}

	private static ModuleDefinitionSource parseDefinition(File file) throws IOException {
		String contents = Files.asCharSource(file, Charsets.UTF_8).read();
		return new ModuleDefinitionSource(file.getPath(), contents);
	}
}
