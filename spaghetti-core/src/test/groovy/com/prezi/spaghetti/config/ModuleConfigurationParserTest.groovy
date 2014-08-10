package com.prezi.spaghetti.config

import com.prezi.spaghetti.ast.parser.AstParserException
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import spock.lang.Specification

class ModuleConfigurationParserTest extends Specification {
	def "Loaded multiple times"() {
		when:
		ModuleConfigurationParser.parse(
				[new ModuleDefinitionSource("C:\\test1.module", "module com.example.test")],
				[],
				[new ModuleDefinitionSource("C:\\test2.module", "module com.example.test")]
		)

		then:
		def ex = thrown AstParserException
		ex.message == "Parse error in C:\\test1.module: module loaded multiple times: com.example.test"
	}

	// See https://github.com/prezi/spaghetti/issues/109
	def "Dependency accessed both directly and transitively"() {
		when:
		ModuleConfigurationParser.parse(
				[new ModuleDefinitionSource("A", "module com.example.testA")],
				[new ModuleDefinitionSource("B", "module com.example.testB struct Point { int x int y }")],
				[new ModuleDefinitionSource("C", "module com.example.testC com.example.testB.Point origin()")]
		)
		then:
		notThrown AstParserException
	}
}
