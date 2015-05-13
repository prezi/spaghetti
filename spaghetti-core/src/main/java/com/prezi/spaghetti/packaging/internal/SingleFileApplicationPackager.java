package com.prezi.spaghetti.packaging.internal;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleElement;
import com.prezi.spaghetti.bundle.internal.ModuleBundleInternal;
import com.prezi.spaghetti.packaging.ApplicationPackageParameters;
import com.prezi.spaghetti.packaging.ModuleWrapper;
import com.prezi.spaghetti.packaging.ModuleWrapperParameters;
import com.prezi.spaghetti.structure.internal.IOAction;
import com.prezi.spaghetti.structure.internal.StructuredAppender;
import com.prezi.spaghetti.structure.internal.StructuredWriter;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SingleFileApplicationPackager extends AbstractApplicationPackager {
	private final ModuleWrapper wrapper;

	public SingleFileApplicationPackager() {
		this.wrapper = new SingleFileModuleWrapper();
	}

	@Override
	public void packageApplicationInternal(final StructuredWriter writer, final ApplicationPackageParameters params) throws IOException {
		for (ModuleBundle bundle : params.bundles) {
			// Extract resources
			StructuredAppender moduleAppender = writer.subAppender(bundle.getName());
			((ModuleBundleInternal) bundle).extract(moduleAppender, EnumSet.of(ModuleBundleElement.RESOURCES));
		}

		final Map<String, ModuleBundle> bundles = Maps.uniqueIndex(params.bundles, new Function<ModuleBundle, String>() {
			@Override
			public String apply(ModuleBundle bundle) {
				return bundle.getName();
			}
		});
		final Map<String, Set<String>> dependencyTree = Maps.newLinkedHashMap();
		for (ModuleBundle bundle : params.bundles) {
			dependencyTree.put(bundle.getName(), bundle.getDependentModules());
		}

		writer.appendFile(params.applicationName, new IOAction<OutputStream>() {
			@Override
			public void execute(final OutputStream out) throws IOException {
				for (String prefix : params.prefixes) {
					IOUtils.write(prefix, out, Charsets.UTF_8);
				}

				IOUtils.write("var modules = [];\n", out, Charsets.UTF_8);
				final List<String> dependencyInitializers = Lists.newArrayList();
				DependencyTreeResolver.resolveDependencies(dependencyTree, new DependencyTreeResolver.DependencyProcessor<String, String>() {
					@Override
					public String processDependency(final String module, Collection<String> dependencies) {
						ModuleBundle bundle = bundles.get(module);
						Collection<String> externalReferences = Collections2.transform(bundle.getExternalDependencies(), new Function<String, String>() {
							@Nullable
							@Override
							public String apply(String dependencyName) {
								return params.externals.containsKey(dependencyName) ?
										params.externals.get(dependencyName) :
										dependencyName;
							}
						});
						Collection<String> moduleReferences = Collections2.transform(dependencies, new Function<String, String>() {
							@Override
							public String apply(String moduleName) {
								return String.format("modules[\"%s\"]", moduleName);
							}
						});
						try {
							String wrappedModule = wrapper.wrap(new ModuleWrapperParameters(bundle));
							dependencyInitializers.add(
									String.format(
										"modules[\"%s\"] = (%s(%s));",
										module,
										wrappedModule,
										Joiner.on(',').join(Iterables.concat(externalReferences, moduleReferences))
									)
							);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						return module;
					}
				});
				IOUtils.write(Joiner.on('\n').join(dependencyInitializers), out, Charsets.UTF_8);
				IOUtils.write("\n", out);

				String wrappedApplication = wrapper.makeApplication(dependencyTree, params.mainModule, params.execute, params.externals);
				IOUtils.write(wrappedApplication, out, Charsets.UTF_8);

				for (String suffix : params.suffixes) {
					IOUtils.write(suffix, out, Charsets.UTF_8);
				}
			}
		});
	}
}
