package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.config.ModuleConfiguration
import com.prezi.spaghetti.generator.AbstractGeneratorFactory
import com.prezi.spaghetti.generator.Generator

public class HaxeGeneratorFactory extends AbstractGeneratorFactory {

	private static def DEFAULT_EXTERNS = [
	        JSON: "haxe.Json",
	]
	public static def EXTERNS = (DEFAULT_EXTERNS + HaxeJsHtmlExterns.EXTERNS).asImmutable()

	HaxeGeneratorFactory() {
		super("haxe", "generates Haxe code")
	}

	@Override
	Set<String> getProtectedSymbols() {
		return [
				// Haxe likes to put this on global objects like Math and String and Date
				"__name__"
		]
	}

	@Override
	Generator createGenerator(ModuleConfiguration configuration)
	{
		return new HaxeGenerator(configuration)
	}
}
