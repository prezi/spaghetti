package com.prezi.spaghetti.javascript;

import com.prezi.spaghetti.AbstractGenerator;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.config.ModuleConfiguration;

import java.io.File;

public class JavaScriptGenerator extends AbstractGenerator {
	public JavaScriptGenerator(ModuleConfiguration config) {
		super(config);
	}

	@Override
	public void generateHeaders(File outputDirectory) {
	}

	@Override
	protected String processModuleJavaScriptInternal(ModuleNode module, String javaScript) {
		return javaScript;
	}

}
