package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.bundle.ModuleBundleElement
import com.prezi.spaghetti.structure.StructuredWriter

/**
 * Created by lptr on 16/05/14.
 */
class ApplicationPackager {
	public static void packageApplicationDirectory(File outputDirectory, ApplicationPackageParameters params) {
		packageApplication(new StructuredWriter.Directory(outputDirectory), params)
	}

	public static void packageApplicationZip(File outputFile, ApplicationPackageParameters params) {
		packageApplication(new StructuredWriter.Zip(outputFile), params)
	}

	protected static void packageApplication(StructuredWriter writer, ApplicationPackageParameters params) {
		writer.init()
		try {
			if (!params.bundles*.name.contains(params.mainModule)) {
				throw new IllegalArgumentException("Main bundle \"${params.mainModule}\" not found among bundles: ${params.bundles*.name.join(", ")}")
			}

			def modulesAppender = writer.subAppender(params.modulesDirectory)

			def appType = params.type
			def wrapper = appType.wrapper
			params.bundles.each { ModuleBundle bundle ->
				// Extract resources
				def moduleDirectory = appType.moduleDirectoryNamer.name(bundle)
				def moduleAppender = modulesAppender.subAppender(moduleDirectory)
				bundle.extract(moduleAppender, EnumSet.of(ModuleBundleElement.resources, ModuleBundleElement.sourcemap))

				// Add JavaScript
				def wrappedJavaScript = wrapper.wrap(bundle.name, bundle.dependentModules, bundle.javaScript)
				def moduleFile = appType.moduleFileNamer.name(bundle)
				moduleAppender.appendFile(moduleFile + ".js", { out -> out << wrappedJavaScript })
			}

			// Add application
			def dependencyTree = params.bundles.collectEntries { bundle ->
				[ bundle.name, bundle.dependentModules ]
			}
			writer.appendFile params.applicationName, { out ->
				out << wrapper.makeApplication(params.baseUrl, params.modulesDirectory, dependencyTree, params.mainModule, params.execute) }
		} finally {
			writer.close()
		}
	}
}
