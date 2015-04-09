package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.generator.HeaderGenerator
import com.prezi.spaghetti.generator.JavaScriptBundleProcessor
import com.prezi.spaghetti.generator.test.LanguageSupportSpecification

import static groovy.io.FileType.FILES

class KotlinGeneratorIntegrationTest extends LanguageSupportSpecification {
	@Override
	protected HeaderGenerator createHeaderGenerator() {
		return new KotlinHeaderGenerator();
	}

	@Override
	protected JavaScriptBundleProcessor createBundleProcessor() {
		return new KotlinJavaScriptBundleProcessor();
	}

	@Override
	protected void compile(ModuleNode module, File compiledJs, File headersDir, File sourceDir) {
		println "out: " + compiledJs
		println "headers:" + getKotlinSourceFiles(headersDir)
		println "sources: " + getKotlinSourceFiles(sourceDir)
		def kotlinHome = System.getenv("KOTLIN_HOME")
		execute([
			"$kotlinHome/bin/kotlinc-js",
			"-main", "noCall",
			"-output",  compiledJs,
			"-library-files", "$kotlinHome/lib/kotlin-jslib.jar",
			"-output-prefix", "$kotlinHome/lib/kotlin-jslib/kotlin.js",
			*getKotlinSourceFiles(headersDir),
			*getKotlinSourceFiles(sourceDir)
		])
	}

	private List<File> getKotlinSourceFiles(File dir) {
		def files = []
		dir.eachFileRecurse(FILES) {
			if (it.name.endsWith(".kt")) {
				files.add it
			}
		}
		return files
	}
}
