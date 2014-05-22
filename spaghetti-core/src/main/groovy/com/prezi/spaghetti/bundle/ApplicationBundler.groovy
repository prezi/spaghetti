package com.prezi.spaghetti.bundle

/**
 * Created by lptr on 16/05/14.
 */
class ApplicationBundler {
	public static void bundleApplicationDirectory(File outputDirectory, ApplicationBundlerParameters params) {
		bundleApplication(new BundleBuilder.Directory(outputDirectory), params)
	}

	public static void bundleApplicationZip(File outputFile, ApplicationBundlerParameters params) {
		bundleApplication(new BundleBuilder.Zip(outputFile), params)
	}

	protected static void bundleApplication(BundleBuilder builder, ApplicationBundlerParameters params) {
		builder.init()
		try {
			if (!params.bundles*.name.contains(params.mainModule)) {
				throw new IllegalArgumentException("Main bundle \"${params.mainModule}\" not found among bundles: ${params.bundles*.name.join(", ")}")
			}

			def modulesAppender = builder.subAppender(params.modulesDirectory)

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
			builder.appendFile params.applicationName, { out ->
				out << wrapper.makeApplication(params.baseUrl, params.modulesDirectory, dependencyTree, params.mainModule, params.execute) }
		} finally {
			builder.close()
		}
	}
}
