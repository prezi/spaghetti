package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.GeneratorFactory
import com.prezi.spaghetti.ModuleConfiguration

/**
 * Created by lptr on 23/11/13.
 */
public class HaxeGeneratorFactory implements GeneratorFactory {

	private static def EXTERNS = [
			"UnicodeString": "String",
			"HTMLCanvasElement": "js.html.CanvasElement",
			"CanvasRenderingContext2D": "js.html.CanvasRenderingContext2D"
		].asImmutable()

	@Override
	String getPlatform()
	{
		return "haxe"
	}

	@Override
	String getDescription()
	{
		return "generates Haxe code"
	}

	@Override
	Map<String, String> getExternMapping()
	{
		return EXTERNS
	}

	@Override
	Generator createGenerator(ModuleConfiguration configuration)
	{
		return new HaxeGenerator(configuration)
	}
}
