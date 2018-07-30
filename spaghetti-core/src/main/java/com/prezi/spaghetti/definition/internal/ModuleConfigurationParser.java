package com.prezi.spaghetti.definition.internal;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.ast.internal.parser.AstParserException;
import com.prezi.spaghetti.ast.internal.parser.MissingTypeResolver;
import com.prezi.spaghetti.ast.internal.parser.ModuleParser;
import com.prezi.spaghetti.ast.internal.parser.ModuleTypeResolver;
import com.prezi.spaghetti.ast.internal.parser.TypeResolver;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleSet;
import com.prezi.spaghetti.definition.EntityWithModuleMetaData;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Parses module definitions for a module.
 */
public final class ModuleConfigurationParser {

	private static Logger logger = LoggerFactory.getLogger(ModuleConfigurationParser.class);
	/**
	 * Parses module definitions for a module.
	 *
	 * @param localModuleSource the source of the local module.
	 * @param dependencies      the loaded bundles.
	 * @return the parsed module configuration.
	 */
	public static ModuleConfiguration parse(ModuleDefinitionSource localModuleSource, String localNamespace, ModuleBundleSet dependencies) {
		Collection<EntityWithModuleMetaData<ModuleDefinitionSource>> directSources =
			Collections2.transform(
				dependencies.getDirectBundles(),
				new Function<ModuleBundle, EntityWithModuleMetaData<ModuleDefinitionSource>>() {
					@Nullable
					@Override
					public EntityWithModuleMetaData<ModuleDefinitionSource> apply(ModuleBundle bundle) {
						return makeModuleSourceWithMetaData(bundle);
					}
				}
			);
		Collection<EntityWithModuleMetaData<ModuleDefinitionSource>> lazySources =
			Collections2.transform(
				dependencies.getLazyBundles(),
				new Function<ModuleBundle, EntityWithModuleMetaData<ModuleDefinitionSource>>() {
					@Nullable
					@Override
					public EntityWithModuleMetaData<ModuleDefinitionSource> apply(ModuleBundle bundle) {
						return makeModuleSourceWithMetaData(bundle);
					}
				}
			);
		Collection<EntityWithModuleMetaData<ModuleDefinitionSource>> transitiveSources =
			Collections2.transform(
				dependencies.getTransitiveBundles(),
				new Function<ModuleBundle, EntityWithModuleMetaData<ModuleDefinitionSource>>() {
					@Nullable
					@Override
					public EntityWithModuleMetaData<ModuleDefinitionSource> apply(ModuleBundle bundle) {
						return makeModuleSourceWithMetaData(bundle);
					}
				}
			);
		return parse(localModuleSource, localNamespace, directSources, lazySources, transitiveSources);
	}

	private static ModuleDefinitionSource makeModuleSource(ModuleBundle bundle) {
		try {
			return DefaultModuleDefinitionSource.fromStringWithLang("loaded from " + bundle.getName() + " bundle", bundle.getDefinition(), bundle.getDefinitionLanguage());
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private static EntityWithModuleMetaData<ModuleDefinitionSource> makeModuleSourceWithMetaData(ModuleBundle bundle) {
		return new DefaultEntityWithModuleMetaData<ModuleDefinitionSource>(
				makeModuleSource(bundle),
				bundle.getFormat()
		);
	}

	/**
	 * Parses module definitions for a module.
	 *
	 * @param localModuleSource       the source of the local module.
	 * @param directModuleSources     the sources of directly dependent modules.
	 * @param lazyModuleSources
	 * @param transitiveModuleSources the sources of transitively dependent modules.
	 * @return the parsed module configuration.
	 */
	public static ModuleConfiguration parse(
			ModuleDefinitionSource localModuleSource,
			String localNamespace,
			Collection<EntityWithModuleMetaData<ModuleDefinitionSource>> directModuleSources,
			Collection<EntityWithModuleMetaData<ModuleDefinitionSource>> lazyModuleSources,
			Collection<EntityWithModuleMetaData<ModuleDefinitionSource>> transitiveModuleSources
	) {
		Set<String> parsedModules = Sets.newLinkedHashSet();

		EntityWithModuleMetaData<ModuleParser> localParser = createParser(
				new DefaultEntityWithModuleMetaData<ModuleDefinitionSource>(localModuleSource, null), localNamespace);
		Iterable<EntityWithModuleMetaData<ModuleParser>> directParsers = createParsersFor(directModuleSources);
		Iterable<EntityWithModuleMetaData<ModuleParser>> lazyParsers = createParsersFor(lazyModuleSources);
		Iterable<EntityWithModuleMetaData<ModuleParser>> transitiveParsers = createParsersFor(transitiveModuleSources);

		TypeResolver resolver = createResolverFor(Iterables.concat(
			Collections.singleton(localParser),
			directParsers,
			lazyParsers,
			transitiveParsers));

		Set<EntityWithModuleMetaData<ModuleNode>> localModules = Sets.newLinkedHashSet();
		Set<EntityWithModuleMetaData<ModuleNode>> directDependentModules = Sets.newLinkedHashSet();
		Set<EntityWithModuleMetaData<ModuleNode>> lazyDependentModules = Sets.newLinkedHashSet();
		Set<EntityWithModuleMetaData<ModuleNode>> transitiveDependentModules = Sets.newLinkedHashSet();
		parseModules(resolver, transitiveParsers, transitiveDependentModules, parsedModules);
		parseModules(resolver, directParsers, directDependentModules, parsedModules);
		parseModules(resolver, lazyParsers, lazyDependentModules, parsedModules);
		parseModules(resolver, Collections.singleton(localParser), localModules, parsedModules);
		if (localModules.isEmpty()) {
			throw new IllegalStateException("No local module found");
		}
		if (localModules.size() > 1) {
			throw new IllegalStateException("More than one local module found: " + localModules);
		}
		return new DefaultModuleConfiguration(
			Iterables.getOnlyElement(localModules).getEntity(),
			directDependentModules,
				lazyDependentModules,
				transitiveDependentModules);
	}

	private static Iterable<EntityWithModuleMetaData<ModuleParser>> createParsersFor(Iterable<EntityWithModuleMetaData<ModuleDefinitionSource>> sources) {
		Set<EntityWithModuleMetaData<ModuleParser>> parsers = Sets.newLinkedHashSet();
		for (EntityWithModuleMetaData<ModuleDefinitionSource> source : sources) {
			parsers.add(createParser(source, null));
		}
		return parsers;
	}

	private static EntityWithModuleMetaData<ModuleParser> createParser(
			EntityWithModuleMetaData<ModuleDefinitionSource> source,
			String namespaceOverride) {
		return new DefaultEntityWithModuleMetaData<ModuleParser>(
			ModuleParser.create(source.getEntity(), namespaceOverride),
			source.getFormat()
		);
	}

	private static TypeResolver createResolverFor(Iterable<EntityWithModuleMetaData<ModuleParser>> parsers) {
		TypeResolver resolver = MissingTypeResolver.INSTANCE;
		for (EntityWithModuleMetaData<ModuleParser> parser : parsers) {
			resolver = new ModuleTypeResolver(resolver, parser.getEntity().getNode());
		}
		return resolver;
	}

	private static void parseModules(TypeResolver resolver, Iterable<EntityWithModuleMetaData<ModuleParser>> parsers, Collection<EntityWithModuleMetaData<ModuleNode>> moduleNodes, Set<String> allModuleNames) {
		for (EntityWithModuleMetaData<ModuleParser> parser : parsers) {
			ModuleNode module = parser.getEntity().parse(resolver);
			if (allModuleNames.contains(module.getName())) {
				throw new AstParserException(module.getSource(), ": module loaded multiple times: " + module.getName());
			}

			allModuleNames.add(module.getName());
			moduleNodes.add(new DefaultEntityWithModuleMetaData<ModuleNode>(
				module,
				parser.getFormat()
			));
		}
	}
}
