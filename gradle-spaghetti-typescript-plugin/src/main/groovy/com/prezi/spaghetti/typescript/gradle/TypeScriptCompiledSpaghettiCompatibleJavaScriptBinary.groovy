package com.prezi.spaghetti.typescript.gradle

import com.prezi.spaghetti.gradle.SpaghettiCompatibleBinaryNamingScheme
import com.prezi.spaghetti.gradle.SpaghettiCompatibleJavaScriptBinary
import com.prezi.typescript.gradle.TypeScriptBinary
import org.gradle.language.base.internal.AbstractBuildableModelElement
import org.gradle.language.base.internal.BinaryInternal
import org.gradle.language.base.internal.BinaryNamingScheme

/**
 * Created by lptr on 09/02/14.
 */
class TypeScriptCompiledSpaghettiCompatibleJavaScriptBinary
		extends AbstractBuildableModelElement implements SpaghettiCompatibleJavaScriptBinary, BinaryInternal {
	private final BinaryNamingScheme namingScheme
	private final TypeScriptBinary binary

	public TypeScriptCompiledSpaghettiCompatibleJavaScriptBinary(TypeScriptBinary binary) {
		this.namingScheme = new SpaghettiCompatibleBinaryNamingScheme(binary.name)
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
