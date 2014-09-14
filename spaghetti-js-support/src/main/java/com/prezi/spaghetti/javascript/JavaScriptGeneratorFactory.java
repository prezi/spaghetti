package com.prezi.spaghetti.javascript;

import com.prezi.spaghetti.generator.AbstractGeneratorFactory;
import com.prezi.spaghetti.generator.Generator;
import com.prezi.spaghetti.generator.GeneratorParameters;

public class JavaScriptGeneratorFactory extends AbstractGeneratorFactory {
	public JavaScriptGeneratorFactory() {
		super("js", "vanilla JavaScript support");
	}

	@Override
	public Generator createGenerator(GeneratorParameters params) {
		return new JavaScriptGenerator(params);
	}

}
