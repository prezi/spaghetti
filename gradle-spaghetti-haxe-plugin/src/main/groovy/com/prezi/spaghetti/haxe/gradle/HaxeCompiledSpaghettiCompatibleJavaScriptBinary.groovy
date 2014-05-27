package com.prezi.spaghetti.haxe.gradle

import com.prezi.haxe.gradle.HaxeBinaryBase
import com.prezi.spaghetti.gradle.SpaghettiCompatibleJavaScriptBinary

/**
 * Created by lptr on 15/02/14.
 */
public interface HaxeCompiledSpaghettiCompatibleJavaScriptBinary
		extends SpaghettiCompatibleJavaScriptBinary {
	HaxeBinaryBase getOriginal()
}
