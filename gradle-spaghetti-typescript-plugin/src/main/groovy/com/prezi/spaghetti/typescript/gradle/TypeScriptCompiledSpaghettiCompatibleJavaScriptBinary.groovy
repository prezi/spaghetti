package com.prezi.spaghetti.typescript.gradle

import com.prezi.spaghetti.gradle.AbstractSpaghettiCompatibleJavaScriptBinary
import com.prezi.typescript.gradle.TypeScriptBinary

/**
 * Created by lptr on 09/02/14.
 */
class TypeScriptCompiledSpaghettiCompatibleJavaScriptBinary
		extends AbstractSpaghettiCompatibleJavaScriptBinary {
	private final TypeScriptBinary original

	public TypeScriptCompiledSpaghettiCompatibleJavaScriptBinary(TypeScriptBinary original) {
		super(original.name, false)
		this.original = original
	}

	@Override
	File getJavaScriptFile() {
		return original.getCompileTask().getOutputFile()
	}
}
