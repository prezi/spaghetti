package com.prezi.spaghetti.config;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.ast.parser.AstParserException;
import com.prezi.spaghetti.ast.parser.MissingTypeResolver;
import com.prezi.spaghetti.ast.parser.ModuleParser;
import com.prezi.spaghetti.ast.parser.ModuleTypeResolver;
import com.prezi.spaghetti.ast.parser.TypeResolver;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;

import java.util.Collection;
import java.util.Set;

public class ModuleConfigurationParser {
	public static ModuleConfiguration parse(Collection<ModuleDefinitionSource> localModuleSources, Collection<ModuleDefinitionSource> dependentModuleSources, Collection<ModuleDefinitionSource> transitiveModuleSources) {
		Set<String> parsedModules = Sets.newLinkedHashSet();
		DefaultModuleConfiguration configNode = new DefaultModuleConfiguration();
		TypeResolver transitiveResolver = parseModules(MissingTypeResolver.INSTANCE, transitiveModuleSources, configNode.getTransitiveDependentModules(), parsedModules);
		TypeResolver directResolver = parseModules(transitiveResolver, dependentModuleSources, configNode.getDirectDependentModules(), parsedModules);
		parseModules(directResolver, localModuleSources, configNode.getLocalModules(), parsedModules);
		return configNode;
	}

	public static TypeResolver parseModules(TypeResolver parentResolver, Collection<ModuleDefinitionSource> sources, final Set<ModuleNode> moduleNodes, final Set<String> allModuleNames) {
		Collection<ModuleParser> parsers = Collections2.transform(sources, new Function<ModuleDefinitionSource, ModuleParser>() {
			@Override
			public ModuleParser apply(ModuleDefinitionSource input) {
				return ModuleParser.create(input);
			}
		});

		TypeResolver resolver = parentResolver;
		for (ModuleParser parser : parsers) {
			resolver = new ModuleTypeResolver(resolver, parser.getModule());
		}

		for (ModuleParser parser : parsers) {
			ModuleNode module = parser.parse(resolver);
			if (allModuleNames.contains(module.getName())) {
				throw new AstParserException(module.getSource(), ": module loaded multiple times: " + module.getName());
			}

			allModuleNames.add(module.getName());
			moduleNodes.add(module);
		}
		return resolver;
	}
}
