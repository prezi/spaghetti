package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.generator.AbstractGeneratorFactory
import com.prezi.spaghetti.generator.Generator
import com.prezi.spaghetti.generator.GeneratorParameters

public class TypeScriptGeneratorFactory extends AbstractGeneratorFactory {

	public static Map<String, String> EXTERNS = Collections.emptyMap()

	TypeScriptGeneratorFactory() {
		super("typescript", "generates TypeScript code")
	}

	@Override
	Generator createGenerator(GeneratorParameters params) {
		return new TypeScriptGenerator(params)
	}
}
