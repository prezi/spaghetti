package com.prezi.spaghetti.gradle

import org.gradle.language.base.internal.AbstractBuildableModelElement
import org.gradle.language.base.internal.BinaryInternal
import org.gradle.language.base.internal.BinaryNamingScheme

/**
 * Created by lptr on 20/05/14.
 */
abstract class AbstractSpaghettiCompatibleJavaScriptBinary extends AbstractBuildableModelElement
		implements SpaghettiCompatibleJavaScriptBinary, BinaryInternal {
	private final BinaryNamingScheme namingScheme
	BundleModule bundleTask
	ObfuscateModule obfuscateTask
	boolean usedForTesting

	AbstractSpaghettiCompatibleJavaScriptBinary(String name, boolean testing) {
		this.namingScheme = new SpaghettiCompatibleBinaryNamingScheme(name)
		this.usedForTesting = testing
	}

	@Override
	BinaryNamingScheme getNamingScheme() {
		return namingScheme
	}

	@Override
	File getSourceMapFile() {
		def outputFile = getJavaScriptFile()
		def sourceMapFile = new File(outputFile.parentFile, outputFile.name + ".map")
		return sourceMapFile.exists() ? sourceMapFile : null
	}

	@Override
	String getDisplayName() {
		return namingScheme.description
	}

	@Override
	String getName() {
		return namingScheme.lifecycleTaskName
	}

	@Override
	String toString() {
		return "${name} Spaghetti binary"
	}
}
