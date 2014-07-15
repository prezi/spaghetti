package com.prezi.spaghetti.typescript.gradle;

import com.prezi.spaghetti.gradle.AbstractSpaghettiCompatibleJavaScriptBinary;
import com.prezi.typescript.gradle.TypeScriptBinaryBase;

import java.io.File;

public class TypeScriptCompiledSpaghettiCompatibleJavaScriptBinary extends AbstractSpaghettiCompatibleJavaScriptBinary {
	public TypeScriptCompiledSpaghettiCompatibleJavaScriptBinary(TypeScriptBinaryBase original, boolean testing) {
		super(original.getName(), testing);
		this.original = original;
	}

	@Override
	public File getJavaScriptFile() {
		return original.getCompileTask().getOutputFile();
	}

	private final TypeScriptBinaryBase original;
}
