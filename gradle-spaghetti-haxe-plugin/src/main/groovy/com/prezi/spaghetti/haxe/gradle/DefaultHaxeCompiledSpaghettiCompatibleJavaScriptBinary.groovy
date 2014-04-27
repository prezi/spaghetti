package com.prezi.spaghetti.haxe.gradle

import com.prezi.haxe.gradle.HaxeBinary
import org.gradle.language.base.internal.AbstractBuildableModelElement
import org.gradle.language.base.internal.BinaryInternal
import org.gradle.language.base.internal.BinaryNamingScheme
import org.gradle.language.base.internal.DefaultBinaryNamingScheme

/**
 * Created by lptr on 09/02/14.
 */
class DefaultHaxeCompiledSpaghettiCompatibleJavaScriptBinary
		extends AbstractBuildableModelElement implements HaxeCompiledSpaghettiCompatibleJavaScriptBinary, BinaryInternal {
	private final BinaryNamingScheme namingScheme
	private final HaxeBinary binary

	public DefaultHaxeCompiledSpaghettiCompatibleJavaScriptBinary(HaxeBinary binary) {
		this.namingScheme = new DefaultBinaryNamingScheme(binary.name, "module", [])
		this.binary = binary
	}

	@Override
	File getJavaScriptFile() {
		return binary.getCompileTask().getOutputFile()
	}

	@Override
	File getSourceMapFile() {
		def outputFile = binary.getCompileTask().getOutputFile()
		def sourceMapFile = new File(outputFile.parentFile, outputFile.name + ".map")
		return sourceMapFile.exists() ? sourceMapFile : null
	}

	@Override
	BinaryNamingScheme getNamingScheme() {
		return namingScheme
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
		return "${name} JS binary"
	}
}
