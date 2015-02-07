package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.generator.HeaderGenerator
import com.prezi.spaghetti.generator.JavaScriptBundleProcessor
import com.prezi.spaghetti.generator.test.LanguageSupportSpecification

import static groovy.io.FileType.FILES

class TypeScriptGeneratorIntegrationTest extends LanguageSupportSpecification {
	@Override
	protected HeaderGenerator createHeaderGenerator() {
		return new TypeScriptHeaderGenerator()
	}

	@Override
	protected JavaScriptBundleProcessor createBundleProcessor() {
		return new TypeScriptJavaScriptBundleProcessor()
	}

	@Override
	protected void compile(ModuleNode module, File compiledJs, File headersDir, File sourceDir) {
		execute([
				"tsc",
				"--out", compiledJs,
				*getTypeScriptFiles(headersDir),
				*getTypeScriptFiles(sourceDir)
		])
	}

	private static List<File> getTypeScriptFiles(File dir) {
		def files = []
		dir.eachFileRecurse(FILES) {
			if (it.name.endsWith(".ts")) {
				files.add it
			}
		}
		return files
	}
}
