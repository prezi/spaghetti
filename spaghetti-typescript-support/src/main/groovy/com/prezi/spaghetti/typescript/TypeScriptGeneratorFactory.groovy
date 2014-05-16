package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.AbstractGeneratorFactory
import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.ModuleConfiguration

/**
 * Created by lptr on 23/11/13.
 */
public class TypeScriptGeneratorFactory extends AbstractGeneratorFactory {

	private static def EXTERNS = [
	        "UnicodeString": "string"
		].asImmutable()

	TypeScriptGeneratorFactory() {
		super("typescript", "generates TypeScript code")
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
