package com.prezi.spaghetti.haxe;

import com.prezi.spaghetti.AbstractGeneratorFactory;
import com.prezi.spaghetti.Generator;
import com.prezi.spaghetti.config.ModuleConfiguration;

public class JavaScriptGeneratorFactory extends AbstractGeneratorFactory {
	public JavaScriptGeneratorFactory() {
		super("js", "vanilla JavaScript support");
	}

	@Override
	public Generator createGenerator(ModuleConfiguration configuration) {
		return new JavaScriptGenerator(configuration);
	}

}
