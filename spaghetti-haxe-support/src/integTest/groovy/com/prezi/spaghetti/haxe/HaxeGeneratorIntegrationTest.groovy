package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.generator.HeaderGenerator
import com.prezi.spaghetti.generator.JavaScriptBundleProcessor
import com.prezi.spaghetti.generator.test.LanguageSupportSpecification

class HaxeGeneratorIntegrationTest extends LanguageSupportSpecification {
	@Override
	protected HeaderGenerator createHeaderGenerator() {
		return new HaxeHeaderGenerator()
	}

	@Override
	protected JavaScriptBundleProcessor createBundleProcessor() {
		return new HaxeJavaScriptBundleProcessor()
	}

	@Override
	protected boolean isTypeScriptDefinitionSupported() {
		return false;
	}

	@Override
	protected void compile(ModuleConfiguration moduleConfig, File compiledJs, File headersDir, File sourceDir) {
		execute "haxe",
				"-js", compiledJs,
				"-cp", headersDir,
				"-cp", sourceDir,
				"--macro", "include(\'" + moduleConfig.localModule.name + "\')"
	}
}
