package com.prezi.spaghetti.javascript;

import com.prezi.spaghetti.config.ModuleConfiguration;
import com.prezi.spaghetti.generator.AbstractGeneratorFactory;
import com.prezi.spaghetti.generator.Generator;

public class JavaScriptGeneratorFactory extends AbstractGeneratorFactory {
	public JavaScriptGeneratorFactory() {
		super("js", "vanilla JavaScript support");
	}

	@Override
	public Generator createGenerator(ModuleConfiguration configuration) {
		return new JavaScriptGenerator(configuration);
	}

}
