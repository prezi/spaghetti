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
	protected boolean isTypeScriptDefinitionSupported() {
		return true;
	}

	@Override
	protected void compile(ModuleNode module, File compiledJs, File headersDir, File sourceDir) {
		execute([
				"tsc",
				"--out", compiledJs,
				*getTypeScriptFiles(headersDir),
				*sortMakingModuleFileLast(getTypeScriptFiles(sourceDir))
		])
	}

	private static List<File> sortMakingModuleFileLast(List<File> files) {
		// The .module.ts file should always be the last argument to "tsc"
		// because it references other code in other files and needs to be
		// the at the bottom of the concatenated JavaScript.
		return files.sort { a, b ->
			def isAModuleDef = a.name.endsWith(".module.ts")
			def isBModuleDef = b.name.endsWith(".module.ts")
			if (isAModuleDef && !isBModuleDef) {
				return 1
			} else if (isBModuleDef && !isAModuleDef) {
				return -1
			} else {
				return a.path <=> b.path
			}
		}
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
