package com.prezi.spaghetti.config

import com.prezi.spaghetti.ast.internal.parser.AstParserException
import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import com.prezi.spaghetti.definition.internal.DefaultEntityWithModuleMetaData
import com.prezi.spaghetti.definition.internal.DefaultModuleDefinitionSource
import com.prezi.spaghetti.definition.internal.ModuleConfigurationParser
import spock.lang.Specification

class ModuleConfigurationParserTest extends Specification {
	def "Loaded multiple times"() {
		when:
		ModuleConfigurationParser.parse(
				DefaultModuleDefinitionSource.fromString("C:\\test1.module", "module com.example.test {}"),
				null,
				[new DefaultEntityWithModuleMetaData<ModuleDefinitionSource>(DefaultModuleDefinitionSource.fromString("C:\\test2.module", "module com.example.test {}"), ModuleFormat.Wrapperless)],
				[],
				[new DefaultEntityWithModuleMetaData<ModuleDefinitionSource>(DefaultModuleDefinitionSource.fromString("C:\\test3.module", "module com.example.test {}"), ModuleFormat.Wrapperless)]
		)

		then:
		def ex = thrown AstParserException
		ex.message == "Parse error in C:\\test2.module: module loaded multiple times: com.example.test"
	}

	def "Dependency accessed from another dependency"() {
		when:
		def config = ModuleConfigurationParser.parse(
				DefaultModuleDefinitionSource.fromString("A", "module com.example.testA {}"),
				null,
				[new DefaultEntityWithModuleMetaData<ModuleDefinitionSource>(DefaultModuleDefinitionSource.fromString("B", "module com.example.testB { struct Point { x: int; y: int; } }"), ModuleFormat.Wrapperless)],
				[],
				[new DefaultEntityWithModuleMetaData<ModuleDefinitionSource>(DefaultModuleDefinitionSource.fromString("C", "module com.example.testC { origin(): com.example.testB.Point; }"), ModuleFormat.Wrapperless)]
		)
		then:
		config.localModule.name == "com.example.testA"
		config.directDependentModules*.entity.name == ["com.example.testB"]
		config.transitiveDependentModules*.entity.name == ["com.example.testC"]
		config.allDependentModules*.name == ["com.example.testB", "com.example.testC"]
		config.allModules*.name == ["com.example.testA", "com.example.testB", "com.example.testC"]
	}
}
