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
				"bundleJsModule",
				"bundleTestJsModule",
				"clean",
				"compileJs",
				"compileTestJs",
				"generateHeaders",
				"js",
				"jsModule",
				"obfuscateJsModule",
				"obfuscateTestJsModule",
				"processSpaghettiResources",
				"spaghetti-platforms",
				"testJs",
				"testJsModule",
				"zipJsModule",
				"zipJsModuleObfuscated",
				"zipTestJsModule",
				"zipTestJsModuleObfuscated",
		]
	}
}
