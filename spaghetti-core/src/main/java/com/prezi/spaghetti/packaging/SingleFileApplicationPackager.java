package com.prezi.spaghetti.packaging;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleElement;
import com.prezi.spaghetti.internal.DependencyTreeResolver;
import com.prezi.spaghetti.structure.IOAction;
import com.prezi.spaghetti.structure.StructuredAppender;
import com.prezi.spaghetti.structure.StructuredWriter;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SingleFileApplicationPackager extends AbstractApplicationPackager {
	private final Wrapper wrapper;

	public SingleFileApplicationPackager() {
		this.wrapper = new SingleFileWrapper();
	}

	@Override
	public void packageApplicationInternal(final StructuredWriter writer, final ApplicationPackageParameters params) throws IOException {
		for (ModuleBundle bundle : params.bundles) {
			// Extract resources
			StructuredAppender moduleAppender = writer.subAppender(bundle.getName());
			bundle.extract(moduleAppender, EnumSet.of(ModuleBundleElement.resources));
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
						Collection<String> dependencyInstances = Collections2.transform(dependencies, new Function<String, String>() {
							@Override
							public String apply(String it) {
								return "modules[\"" + it + "\"]";
							}
						});
						try {
							String wrappedModule = wrapper.wrap(new ModuleWrappingParameters(bundle));
							dependencyInitializers.add("modules[\"" + module + "\"] = (" + wrappedModule + "(" + Joiner.on(',').join(dependencyInstances) + "));");
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						return module;
					}
				});
				IOUtils.write(Joiner.on('\n').join(dependencyInitializers), out, Charsets.UTF_8);
				IOUtils.write("\n", out);

				String wrappedApplication = wrapper.makeApplication(params.baseUrl, params.modulesDirectory, dependencyTree, params.mainModule, params.execute);
				IOUtils.write(wrappedApplication, out, Charsets.UTF_8);

				for (String suffix : params.suffixes) {
					IOUtils.write(suffix, out, Charsets.UTF_8);
				}
			}
		});
	}
}
