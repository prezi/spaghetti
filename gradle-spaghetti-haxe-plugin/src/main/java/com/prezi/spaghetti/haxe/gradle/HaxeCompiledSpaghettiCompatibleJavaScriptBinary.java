package com.prezi.spaghetti.haxe.gradle;

import com.prezi.haxe.gradle.HaxeBinaryBase;
import com.prezi.spaghetti.gradle.SpaghettiCompatibleJavaScriptBinary;

public interface HaxeCompiledSpaghettiCompatibleJavaScriptBinary extends SpaghettiCompatibleJavaScriptBinary {
	HaxeBinaryBase getOriginal();
}
