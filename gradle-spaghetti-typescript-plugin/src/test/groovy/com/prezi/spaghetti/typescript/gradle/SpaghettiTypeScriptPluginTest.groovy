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
				"clean",
				"compileJs",
				"compileTestJs",
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
				"zipJsModule",
				"zipJsModuleObfuscated",
				"zipTestJsModule",
				"zipTestJsModuleObfuscated",
		]
	}
}
