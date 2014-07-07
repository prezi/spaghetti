package com.prezi.spaghetti.typescript.gradle

import com.prezi.spaghetti.gradle.AbstractSpaghettiCompatibleJavaScriptBinary
import com.prezi.typescript.gradle.TypeScriptBinaryBase

class TypeScriptCompiledSpaghettiCompatibleJavaScriptBinary
		extends AbstractSpaghettiCompatibleJavaScriptBinary {
	private final TypeScriptBinaryBase original

	public TypeScriptCompiledSpaghettiCompatibleJavaScriptBinary(TypeScriptBinaryBase original, boolean testing) {
		super(original.name, testing)
		this.original = original
	}

	@Override
	File getJavaScriptFile() {
		return original.getCompileTask().getOutputFile()
	}
}
