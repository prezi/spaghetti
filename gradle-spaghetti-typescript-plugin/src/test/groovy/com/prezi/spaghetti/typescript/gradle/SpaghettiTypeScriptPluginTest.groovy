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
				"bundleDefJsModule",
				"bundleDefTestJsModule",
				"bundleJsModule",
				"bundleTestJsModule",
				"check",
				"compileJs",
				"compileTestJs",
				"concatenateJs",
				"concatenateTestJs",
				"generateHeaders",
				"generateStubs",
				"generateTestHeaders",
				"js",
				"jsModule",
				"mergeDtsForJs",
				"obfuscateJsModule",
				"obfuscateTestJsModule",
				"processSpaghettiResources",
				"testJs",
				"testJsModule",
				"zipJsModule",
				"zipJsModuleDefinition",
				"zipJsModuleObfuscated",
				"zipTestJsModule",
				"zipTestJsModuleDefinition",
				"zipTestJsModuleObfuscated",
		]
	}
}
