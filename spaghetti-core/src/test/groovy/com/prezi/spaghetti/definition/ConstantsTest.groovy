package com.prezi.spaghetti.definition

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lptr on 21/05/14.
 */
class ConstantsTest extends Specification {
	@Unroll
	def "Parse entry decl: #constantEntryDecl"() {
		def parserContext = ModuleDefinitionParser.createParser(new ModuleDefinitionSource("test", constantEntryDecl))
		def entry = parserContext.parser.constEntryDecl()

		expect:
		!parserContext.listener.inError

		println entry.dump()

		!typeDef || (entry."${typeDef}Type"() != null)
		entry.name.text == name
		entry."${valueType}Value".text == value

		where:
		constantEntryDecl        | name    | value     | typeDef  | valueType
		'tibor = true'           | "tibor" | "true"    | null     | "bool"
		'bool tibor = false'     | "tibor" | "false"   | "bool"   | "bool"
		'tibor = 1'              | "tibor" | "1"       | null     | "int"
		'int tibor = 1'          | "tibor" | "1"       | "int"    | "int"
		'tibor = 1.0'            | "tibor" | "1.0"     | null     | "float"
		'float tibor = 1.0'      | "tibor" | "1.0"     | "float"  | "float"
		'tibor = "lajos"'        | "tibor" | '"lajos"' | null     | "string"
		'string tibor = "lajos"' | "tibor" | '"lajos"' | "string" | "string"
	}
}
