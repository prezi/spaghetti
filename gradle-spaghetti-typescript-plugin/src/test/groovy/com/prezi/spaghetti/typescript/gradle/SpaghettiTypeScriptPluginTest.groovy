package com.prezi.spaghetti.typescript.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by lptr on 29/04/14.
 */
class SpaghettiTypeScriptPluginTest extends Specification {
	def "empty project creates all necessary tasks"() {
		def project = ProjectBuilder.builder().build()
		project.apply plugin: "spaghetti-typescript"

		expect:
		project.tasks*.name.sort() == [
				"assemble",
				"bundleModule",
				"bundleTestModule",
				"clean",
				"compile",
				"compileTest",
				"generateHeaders",
				"main",
				"mainModule",
				"obfuscateModule",
				"obfuscateTestModule",
				"processSpaghettiResources",
				"spaghetti-platforms",
				"test",
				"testModule",
				"zipModule",
				"zipModuleObfuscated",
				"zipTestModule",
				"zipTestModuleObfuscated",
		]
	}
}
