package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.GeneratorFactory
import com.prezi.spaghetti.ModuleConfiguration

/**
 * Created by lptr on 23/11/13.
 */
public class TypeScriptGeneratorFactory implements GeneratorFactory {

	private static def EXTERNS = [
	        "UnicodeString": "string"
		].asImmutable()

	@Override
	String getPlatform()
	{
		return "typescript"
	}

	@Override
	String getDescription()
	{
		return "generates TypeScript code"
	}

	@Override
	Map<String, String> getExternMapping()
	{
		return EXTERNS
	}

	@Override
	Generator createGenerator(ModuleConfiguration configuration)
	{
		return new TypeScriptGenerator(configuration)
	}
}
