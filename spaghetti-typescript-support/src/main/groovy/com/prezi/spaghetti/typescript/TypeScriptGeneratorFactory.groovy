package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.AbstractGeneratorFactory
import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.config.ModuleConfiguration

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
