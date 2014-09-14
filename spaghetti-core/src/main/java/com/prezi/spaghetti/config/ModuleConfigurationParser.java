package com.prezi.spaghetti.config;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.ast.internal.parser.AstParserException;
import com.prezi.spaghetti.ast.internal.parser.MissingTypeResolver;
import com.prezi.spaghetti.ast.internal.parser.ModuleParser;
import com.prezi.spaghetti.ast.internal.parser.ModuleTypeResolver;
import com.prezi.spaghetti.ast.internal.parser.TypeResolver;
import com.prezi.spaghetti.config.internal.DefaultModuleConfiguration;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;

import java.util.Collection;
import java.util.Set;

public class ModuleConfigurationParser {
	public static ModuleConfiguration parse(Collection<ModuleDefinitionSource> localModuleSources, Collection<ModuleDefinitionSource> dependentModuleSources, Collection<ModuleDefinitionSource> transitiveModuleSources) {
		Set<String> parsedModules = Sets.newLinkedHashSet();
		DefaultModuleConfiguration configNode = new DefaultModuleConfiguration();

		Collection<ModuleParser> transitiveParsers = createParsersFor(transitiveModuleSources);
		Collection<ModuleParser> directParsers = createParsersFor(dependentModuleSources);
		Collection<ModuleParser> localParsers = createParsersFor(localModuleSources);

		TypeResolver resolver = createResolverFor(Iterables.concat(localParsers, directParsers, transitiveParsers));

		parsedModules(resolver, transitiveParsers, configNode.getTransitiveDependentModules(), parsedModules);
		parsedModules(resolver, directParsers, configNode.getDirectDependentModules(), parsedModules);
		parsedModules(resolver, localParsers, configNode.getLocalModules(), parsedModules);

		return configNode;
	}

	private static Collection<ModuleParser> createParsersFor(Collection<ModuleDefinitionSource> sources) {
		Set<ModuleParser> parsers = Sets.newLinkedHashSet();
		for (ModuleDefinitionSource source : sources) {
			parsers.add(ModuleParser.create(source));
		}
		return parsers;
	}

	private static TypeResolver createResolverFor(Iterable<ModuleParser> parsers) {
		TypeResolver resolver = MissingTypeResolver.INSTANCE;
		for (ModuleParser parser : parsers) {
			resolver = new ModuleTypeResolver(resolver, parser.getModule());
		}
		return resolver;
	}

	private static void parsedModules(TypeResolver resolver, Collection<ModuleParser> parsers, Collection<ModuleNode> moduleNodes, Set<String> allModuleNames) {
		for (ModuleParser parser : parsers) {
			ModuleNode module = parser.parse(resolver);
			if (allModuleNames.contains(module.getName())) {
				throw new AstParserException(module.getSource(), ": module loaded multiple times: " + module.getName());
			}

			allModuleNames.add(module.getName());
			moduleNodes.add(module);
		}
	}
}
