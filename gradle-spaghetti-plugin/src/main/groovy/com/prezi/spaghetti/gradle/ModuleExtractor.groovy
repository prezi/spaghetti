package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle
import org.gradle.api.artifacts.Configuration

/**
 * Created by lptr on 05/12/13.
 */
class ModuleExtractor {
	public static List<ModuleBundle> extractModules(Configuration configuration, File outputDirectory) {
		outputDirectory.mkdirs()
		def bundles = ModuleDefinitionLookup.getAllBundles(configuration)
		bundles.each { bundle ->
			def outputFile = new File(outputDirectory, bundle.name + ".js")
			outputFile.delete()
			outputFile << bundle.bundledJavaScript
		}
		return bundles
	}
}
