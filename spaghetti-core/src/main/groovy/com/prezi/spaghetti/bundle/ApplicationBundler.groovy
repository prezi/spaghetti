package com.prezi.spaghetti.bundle

/**
 * Created by lptr on 16/05/14.
 */
class ApplicationBundler {
	private static final String MODULES_DIRECTORY = "modules"
	private static final String APPLICATION_NAME = "application.js"

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

			def modulesAppender = builder.subAppender(MODULES_DIRECTORY)
			params.bundles.each { ModuleBundle bundle ->
				// Extract resources
				def moduleAppender = modulesAppender.subAppender(bundle.name)
				bundle.extract(moduleAppender, EnumSet.of(ModuleBundleElement.resources, ModuleBundleElement.sourcemap))

				// Add JavaScript
				def wrappedJavaScript = params.wrapper.wrap(bundle.name, bundle.dependentModules, bundle.javaScript)
				moduleAppender.appendFile(bundle.name + ".js", { out -> out << wrappedJavaScript })
			}
			// Add application
			builder.appendFile APPLICATION_NAME, { out -> out << params.wrapper.makeApplication(MODULES_DIRECTORY, params.bundles*.name, params.mainModule) }
		} finally {
			builder.close()
		}
	}
}
