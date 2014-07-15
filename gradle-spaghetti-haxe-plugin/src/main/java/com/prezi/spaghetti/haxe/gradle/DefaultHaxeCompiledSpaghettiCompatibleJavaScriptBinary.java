package com.prezi.spaghetti.haxe.gradle;

import com.prezi.haxe.gradle.HaxeBinaryBase;
import com.prezi.spaghetti.gradle.AbstractSpaghettiCompatibleJavaScriptBinary;

import java.io.File;

public class DefaultHaxeCompiledSpaghettiCompatibleJavaScriptBinary extends AbstractSpaghettiCompatibleJavaScriptBinary implements HaxeCompiledSpaghettiCompatibleJavaScriptBinary {
	public DefaultHaxeCompiledSpaghettiCompatibleJavaScriptBinary(HaxeBinaryBase original, boolean testing) {
		super(original.getName(), testing);
		this.original = original;
	}

	@Override
	public HaxeBinaryBase getOriginal() {
		return original;
	}

	@Override
	public File getJavaScriptFile() {
		return original.getCompileTask().getOutputFile();
	}

	private final HaxeBinaryBase original;
}
