package com.prezi.spaghetti.config

import com.prezi.spaghetti.ast.internal.parser.AstParserException
import com.prezi.spaghetti.definition.internal.DefaultModuleDefinitionSource
import com.prezi.spaghetti.definition.internal.ModuleConfigurationParser
import spock.lang.Specification

class ModuleConfigurationParserTest extends Specification {
	def "Loaded multiple times"() {
		when:
		ModuleConfigurationParser.parse(
				DefaultModuleDefinitionSource.fromString("C:\\test1.module", "module com.example.test {}"),
				[DefaultModuleDefinitionSource.fromString("C:\\test2.module", "module com.example.test {}")],
				[DefaultModuleDefinitionSource.fromString("C:\\test3.module", "module com.example.test {}")]
		)

		then:
		def ex = thrown AstParserException
		ex.message == "Parse error in C:\\test2.module: module loaded multiple times: com.example.test"
	}

	def "Dependency accessed from another dependency"() {
		when:
		def config = ModuleConfigurationParser.parse(
				DefaultModuleDefinitionSource.fromString("A", "module com.example.testA {}"),
				[DefaultModuleDefinitionSource.fromString("B", "module com.example.testB { struct Point { x: int; y: int; } }")],
				[DefaultModuleDefinitionSource.fromString("C", "module com.example.testC { origin(): com.example.testB.Point; }")]
		)
		then:
		config.localModule.name == "com.example.testA"
		config.directDependentModules*.name == ["com.example.testB"]
		config.transitiveDependentModules*.name == ["com.example.testC"]
		config.allDependentModules*.name == ["com.example.testB", "com.example.testC"]
		config.allModules*.name == ["com.example.testA", "com.example.testB", "com.example.testC"]
	}
}
