package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.bundle.ModuleBundleElement
import com.prezi.spaghetti.structure.StructuredWriter

/**
 * Created by lptr on 16/05/14.
 */
abstract class AbstractStructuredApplicationPackager extends AbstractApplicationPackager {

	protected final Wrapper wrapper

	AbstractStructuredApplicationPackager(Wrapper wrapper) {
		this.wrapper = wrapper
	}

	@Override
	void packageApplicationInternal(StructuredWriter writer, ApplicationPackageParameters params) {
		def modulesAppender = writer.subAppender(params.modulesDirectory)

		params.bundles.each { ModuleBundle bundle ->
			// Extract resources
			def moduleAppender = modulesAppender.subAppender(bundle.name)
			bundle.extract(moduleAppender, EnumSet.of(ModuleBundleElement.resources, ModuleBundleElement.sourcemap))

			// Add JavaScript
			def wrappedJavaScript = wrapper.wrap(bundle.name, bundle.dependentModules, bundle.javaScript)
			def moduleFile = getModuleFileName(bundle)
			moduleAppender.appendFile(moduleFile, { out -> out << wrappedJavaScript })
		}

		// Add application
		def dependencyTree = params.bundles.collectEntries { bundle ->
			[ bundle.name, bundle.dependentModules ]
		}
		writer.appendFile params.applicationName, { out ->
			params.prefixes.each { out << it }
			out << wrapper.makeApplication(params.baseUrl, params.modulesDirectory, dependencyTree, params.mainModule, params.execute)
			params.suffixes.each { out << it }
		}
	}

	abstract String getModuleFileName(ModuleBundle bundle)
}
