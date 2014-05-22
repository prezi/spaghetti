package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.DefinitionParserHelper
import spock.lang.Specification

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptModuleProxyGeneratorVisitorTest extends Specification {
	def "generate"() {
		def module = new DefinitionParserHelper().parse("""module com.example.test

/**
 * Initializes module.
 */
void initModule(int a, int b)
string doSomething()
""")
		def visitor = new TypeScriptModuleProxyGeneratorVisitor(module)

		expect:
		visitor.processModule() == """export var Test:com.example.test.Test = __config["__modules"]["com.example.test"].__module;
"""
	}
}
