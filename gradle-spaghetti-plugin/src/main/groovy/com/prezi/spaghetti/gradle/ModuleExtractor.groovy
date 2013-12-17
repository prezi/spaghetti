package com.prezi.spaghetti.gradle

import org.gradle.api.artifacts.Configuration

/**
 * Created by lptr on 05/12/13.
 */
class ModuleExtractor {
	public static void extractModules(Configuration configuration, File outputDirectory) {
		outputDirectory.mkdirs()
		def bundles = ModuleDefinitionLookup.getAllBundles(configuration)
		bundles.each { bundle ->
			def outputFile = new File(outputDirectory, bundle.name.fullyQualifiedName + ".js")
			outputFile.delete()
			outputFile << bundle.bundledJavaScript
		}
	}
}
