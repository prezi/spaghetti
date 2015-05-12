package com.prezi.spaghetti.definition.internal;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.ModuleDefinitionSource;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.ast.internal.DefaultModuleDefinitionSource;
import com.prezi.spaghetti.ast.internal.parser.AstParserException;
import com.prezi.spaghetti.ast.internal.parser.MissingTypeResolver;
import com.prezi.spaghetti.ast.internal.parser.ModuleParser;
import com.prezi.spaghetti.ast.internal.parser.ModuleTypeResolver;
import com.prezi.spaghetti.ast.internal.parser.TypeResolver;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleSet;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.definition.internal.DefaultModuleConfiguration;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Parses module definitions for a module.
 */
public final class ModuleConfigurationParser {
	/**
	 * Parses module definitions for a module.
	 *
	 * @param localModuleSource the source of the local module.
	 * @param dependencies      the loaded bundles.
	 * @return the parsed module configuration.
	 */
	public static ModuleConfiguration parse(ModuleDefinitionSource localModuleSource, ModuleBundleSet dependencies) {
		Collection<ModuleDefinitionSource> directSources = makeModuleSources(dependencies.getDirectBundles());
		Collection<ModuleDefinitionSource> transitiveSources = makeModuleSources(dependencies.getTransitiveBundles());
		return parse(localModuleSource, directSources, transitiveSources);
	}

	private static Collection<ModuleDefinitionSource> makeModuleSources(Set<ModuleBundle> bundles) {
		return Collections2.transform(bundles, new Function<ModuleBundle, ModuleDefinitionSource>() {
			@Override
			public ModuleDefinitionSource apply(ModuleBundle bundle) {
				try {
					return DefaultModuleDefinitionSource.fromString("module: " + bundle.getName(), bundle.getDefinition());
				} catch (IOException e) {
					throw Throwables.propagate(e);
				}
			}
		});
	}

	/**
	 * Parses module definitions for a module.
	 *
	 * @param localModuleSource       the source of the local module.
	 * @param directModuleSources     the sources of directly dependent modules.
	 * @param transitiveModuleSources the sources of transitively dependent modules.
	 * @return the parsed module configuration.
	 */
	public static ModuleConfiguration parse(ModuleDefinitionSource localModuleSource, Collection<ModuleDefinitionSource> directModuleSources, Collection<ModuleDefinitionSource> transitiveModuleSources) {
		Set<String> parsedModules = Sets.newLinkedHashSet();

		Collection<ModuleParser> localParsers = createParsersFor(Collections.singleton(localModuleSource));
		Collection<ModuleParser> directParsers = createParsersFor(directModuleSources);
		Collection<ModuleParser> transitiveParsers = createParsersFor(transitiveModuleSources);

		TypeResolver resolver = createResolverFor(Iterables.concat(localParsers, directParsers, transitiveParsers));

		Set<ModuleNode> localModules = Sets.newLinkedHashSet();
		Set<ModuleNode> directDependentModules = Sets.newLinkedHashSet();
		Set<ModuleNode> transitiveDependentModules = Sets.newLinkedHashSet();
		parseModules(resolver, transitiveParsers, transitiveDependentModules, parsedModules);
		parseModules(resolver, directParsers, directDependentModules, parsedModules);
		parseModules(resolver, localParsers, localModules, parsedModules);
		if (localModules.isEmpty()) {
			throw new IllegalStateException("No local module found");
		}
		if (localModules.size() > 1) {
			throw new IllegalStateException("More than one local module found: " + localModules);
		}
		return new DefaultModuleConfiguration(Iterables.getOnlyElement(localModules), directDependentModules, transitiveDependentModules);
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
			resolver = new ModuleTypeResolver(resolver, parser.getNode());
		}
		return resolver;
	}

	private static void parseModules(TypeResolver resolver, Collection<ModuleParser> parsers, Collection<ModuleNode> moduleNodes, Set<String> allModuleNames) {
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
