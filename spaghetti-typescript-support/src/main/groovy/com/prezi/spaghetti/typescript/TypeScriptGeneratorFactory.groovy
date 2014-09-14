package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.config.ModuleConfiguration
import com.prezi.spaghetti.generator.AbstractGeneratorFactory
import com.prezi.spaghetti.generator.Generator

public class TypeScriptGeneratorFactory extends AbstractGeneratorFactory {

	public static Map<String, String> EXTERNS = Collections.emptyMap()

	TypeScriptGeneratorFactory() {
		super("typescript", "generates TypeScript code")
	}

	@Override
	Generator createGenerator(ModuleConfiguration configuration) {
		return new TypeScriptGenerator(configuration)
	}
}
