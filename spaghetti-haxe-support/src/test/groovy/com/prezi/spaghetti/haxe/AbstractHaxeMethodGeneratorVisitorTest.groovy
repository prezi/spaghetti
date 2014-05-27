package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.FQName
import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.ModuleDefinitionParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lptr on 27/05/14.
 */
class AbstractHaxeMethodGeneratorVisitorTest extends Specification {
	@Unroll
	def "parse #methodDef"() {
		def module = Mock(ModuleDefinition)
		module.resolveName(FQName.fromString("Thing")) >> FQName.fromString("com.example.test.Thing")
		def visitor = new AbstractHaxeMethodGeneratorVisitor(module) {}

		def parserContext = ModuleDefinitionParser.createParser(new ModuleDefinitionSource("test", methodDef))
		def methodCtx = parserContext.parser.methodDefinition()
		def result = visitor.visitMethodDefinition(methodCtx)

		expect:
		result == "\t$expected\n"

		where:
		methodDef                  | expected
		"int add(int a, int b)"    | "function add(a:Int, b:Int):Int;"
		"<T, U> T add(int a, U u)" | "function add<T, U>(a:Int, u:U):T;"
		"<T> Thing<T> get()"       | "function get<T>():com.example.test.Thing<T>;"
	}
}
