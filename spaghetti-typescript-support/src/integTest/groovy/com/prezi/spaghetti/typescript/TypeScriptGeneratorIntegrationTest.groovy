package com.prezi.spaghetti.typescript

import org.apache.commons.io.FileUtils
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.generator.HeaderGenerator
import com.prezi.spaghetti.generator.JavaScriptBundleProcessor
import com.prezi.spaghetti.generator.test.LanguageSupportSpecification
import com.prezi.spaghetti.obfuscation.ClosureTarget
import com.prezi.spaghetti.obfuscation.internal.ClosureCompiler

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

	protected String makeTsConfig(File moduleFile, File outDir, File headersDir, File sourcesDir) {
		return """{
  "files": [
    "${moduleFile.absolutePath}",
  ],
  "compilerOptions": {
    "module": "commonjs",
    "target": "ES6",
    "outDir": "${outDir.absolutePath}",
    "baseUrl": "/",
    "paths": {
      "*": [
        "${headersDir.absolutePath}/*",
        "${sourcesDir.absolutePath}/*",
      ]
    },
  }
}
"""
	}

	@Override
	protected void compile(ModuleConfiguration moduleConfig, File compiledJs, File headersDir, File sourcesDir) {
		def tscDir = new File(compiledJs.parentFile, "tsc")
		def closureDir = new File(compiledJs.parentFile, "closure")
		def nodeModulesDir = new File(closureDir, "node_modules")

		def content = makeTsConfig(
			getFileEndingWith("module.ts", [ headersDir, sourcesDir ]),
			tscDir,
			headersDir,
			sourcesDir)

		def tsconfigFile = new File(compiledJs.parentFile, "tsconfig.json")
		tsconfigFile.text = content;

		execute([ "tsc", "-p", tsconfigFile ])

		nodeModulesDir.mkdirs()
		tscDir.eachFileRecurse(FILES) { f ->
			if (f.name.endsWith(".js")) {
				FileUtils.copyFileToDirectory(f, nodeModulesDir)
			}
		}

		ClosureCompiler.createExternAccessorsForConcat(nodeModulesDir, moduleConfig);

		File mainEntryPoint = new File(nodeModulesDir, "_spaghetti-entry.js");
		ClosureCompiler.writeMainEntryPoint(
			mainEntryPoint,
			[ getFileEndingWith("module.js", [ nodeModulesDir ]) ],
			moduleConfig.getLocalModule().getName());

		ClosureCompiler.concat(
			closureDir,
			compiledJs,
			mainEntryPoint,
			[ closureDir ],
			[],
			ClosureTarget.ES6);

	}

	private static File getFileEndingWith(String suffix, List<File> dirs) {
		def files = []
		dirs.each { dir ->
			dir.eachFileRecurse(FILES) {
				if (it.name.endsWith(suffix)) {
					files.add(it)
				}
			}
		}
		assert files.size() == 1
		return files[0]
	}
}
