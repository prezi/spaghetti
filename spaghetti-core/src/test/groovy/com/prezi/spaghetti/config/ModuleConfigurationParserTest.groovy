package com.prezi.spaghetti.config

import com.prezi.spaghetti.ast.internal.parser.AstParserException
import com.prezi.spaghetti.definition.ModuleConfigurationParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import spock.lang.Specification

class ModuleConfigurationParserTest extends Specification {
	def "Loaded multiple times"() {
		when:
		ModuleConfigurationParser.parse(
				ModuleDefinitionSource.fromString("C:\\test1.module", "module com.example.test"),
				[],
				[ModuleDefinitionSource.fromString("C:\\test2.module", "module com.example.test")]
		)

		then:
		def ex = thrown AstParserException
		ex.message == "Parse error in C:\\test1.module: module loaded multiple times: com.example.test"
	}

	// See https://github.com/prezi/spaghetti/issues/109
	def "Dependency accessed both directly and transitively"() {
		when:
		ModuleConfigurationParser.parse(
				ModuleDefinitionSource.fromString("A", "module com.example.testA"),
				[ModuleDefinitionSource.fromString("B", "module com.example.testB struct Point { int x int y }")],
				[ModuleDefinitionSource.fromString("C", "module com.example.testC com.example.testB.Point origin()")]
		)
		then:
		notThrown AstParserException
	}
}
