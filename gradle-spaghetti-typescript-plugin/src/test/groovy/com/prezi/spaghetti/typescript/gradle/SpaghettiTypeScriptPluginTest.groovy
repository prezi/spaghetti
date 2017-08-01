package com.prezi.spaghetti.typescript.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class SpaghettiTypeScriptPluginTest extends Specification {
	def "empty project creates all necessary tasks"() {
		def project = ProjectBuilder.builder().build()
		project.apply plugin: "spaghetti-typescript"

		expect:
		project.tasks*.name.sort() == [
				"assemble",
				"build",
				"bundleJsModule",
				"bundleTestJsModule",
				"check",
				"compileJs",
				"compileTestJs",
				"copyDtsForJs",
				"generateHeaders",
				"generateStubs",
				"generateTestHeaders",
				"js",
				"jsModule",
				"obfuscateJsModule",
				"obfuscateTestJsModule",
				"processSpaghettiResources",
				"testJs",
				"testJsModule",
				"verifyDtsForJsModule",
				"verifyDtsForTestJsModule",
				"zipJsModule",
				"zipJsModuleObfuscated",
				"zipTestJsModule",
				"zipTestJsModuleObfuscated",
		]
	}
}
