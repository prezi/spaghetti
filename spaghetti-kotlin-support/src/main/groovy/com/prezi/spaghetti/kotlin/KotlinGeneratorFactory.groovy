package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.generator.AbstractGeneratorFactory
import com.prezi.spaghetti.generator.Generator
import com.prezi.spaghetti.generator.GeneratorParameters

public class KotlinGeneratorFactory extends AbstractGeneratorFactory {

	public static def EXTERNS = [:].asImmutable()

	KotlinGeneratorFactory() {
		super("kotlin", "generates Kotlin code")
	}

	@Override
	Set<String> getProtectedSymbols() {
		return []
	}

	@Override
	Generator createGenerator(GeneratorParameters params) {
		return new KotlinGenerator(params)
	}
}
