package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.GeneratorFactory
import com.prezi.spaghetti.ModuleConfiguration

/**
 * Created by lptr on 23/11/13.
 */
public class TypeScriptGeneratorFactory implements GeneratorFactory {

	@Override
	String getPlatform()
	{
		return "typescript"
	}

	@Override
	Generator createGenerator(ModuleConfiguration configuration)
	{
		return new TypeScriptGenerator(configuration)
	}
}
