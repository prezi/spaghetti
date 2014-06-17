package com.prezi.spaghetti.haxe.gradle

import com.prezi.haxe.gradle.HaxeBinaryBase
import com.prezi.spaghetti.gradle.AbstractSpaghettiCompatibleJavaScriptBinary

class DefaultHaxeCompiledSpaghettiCompatibleJavaScriptBinary
		extends AbstractSpaghettiCompatibleJavaScriptBinary
		implements HaxeCompiledSpaghettiCompatibleJavaScriptBinary {
	private final HaxeBinaryBase original

	public DefaultHaxeCompiledSpaghettiCompatibleJavaScriptBinary(HaxeBinaryBase original, boolean testing) {
		super(original.name, testing)
		this.original = original
	}

	@Override
	HaxeBinaryBase getOriginal() {
		return original
	}

	@Override
	File getJavaScriptFile() {
		return original.getCompileTask().getOutputFile()
	}
}
