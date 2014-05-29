package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.bundle.ModuleBundleElement
import com.prezi.spaghetti.internal.DependencyTreeResolver
import com.prezi.spaghetti.structure.StructuredWriter

/**
 * Created by lptr on 16/05/14.
 */
class SingleFileApplicationPackager extends AbstractApplicationPackager {

	private final Wrapper wrapper

	SingleFileApplicationPackager() {
		this.wrapper = new SingleFileWrapper()
	}

	@Override
	void packageApplicationInternal(StructuredWriter writer, ApplicationPackageParameters params) {
		params.bundles.each { ModuleBundle bundle ->
			// Extract resources
			def moduleAppender = writer.subAppender(bundle.name)
			bundle.extract(moduleAppender, EnumSet.of(ModuleBundleElement.resources))
		}

		Map<String, ModuleBundle> bundles = params.bundles.collectEntries { bundle ->
			[ bundle.name, bundle ]
		}
		Map<String, Set<String>> dependencyTree = params.bundles.collectEntries { bundle ->
			[ bundle.name, bundle.dependentModules ]
		}
		writer.appendFile(params.applicationName, { out ->
			params.prefixes.each { out << it }
			out << "var modules = [];\n"
			DependencyTreeResolver.resolveDependencies(dependencyTree, new DependencyTreeResolver.DependencyProcessor<String, String>() {
				@Override
				String processDependency(String module, Collection<String> dependencies) {
					def bundle = bundles.get(module)
					def dependencyInstances = dependencies.collect { "modules[\"$it\"]" }
					out << "modules[\"${module}\"] = (" + wrapper.wrap(module, dependencies, bundle.javaScript) + "(${dependencyInstances.join(",")}));\n"
					return module
				}
			})
			out << wrapper.makeApplication(params.baseUrl, params.modulesDirectory, dependencyTree, params.mainModule, params.execute)
			params.suffixes.each { out << it }
		})
	}
}
