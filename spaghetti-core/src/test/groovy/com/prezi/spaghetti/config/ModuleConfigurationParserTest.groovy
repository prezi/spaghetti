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
}
