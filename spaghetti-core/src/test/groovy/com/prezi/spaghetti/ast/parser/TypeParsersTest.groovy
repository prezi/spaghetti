package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.ArrayedTypeReference
import com.prezi.spaghetti.ast.AstTestBase
import com.prezi.spaghetti.ast.PrimitiveType
import com.prezi.spaghetti.ast.PrimitiveTypeReference
import com.prezi.spaghetti.ast.TypeChain
import com.prezi.spaghetti.ast.TypeReference
import com.prezi.spaghetti.definition.ModuleDefinitionParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import spock.lang.Unroll

import static com.prezi.spaghetti.ast.PrimitiveTypeReference.INT
import static com.prezi.spaghetti.ast.PrimitiveTypeReference.STRING
import static com.prezi.spaghetti.ast.VoidTypeReference.VOID

/**
 * Created by lptr on 21/05/14.
 */
class TypeParsersTest extends AstTestBase {
	@Unroll
	def "primitives #definition"() {
		def type = parse PrimitiveTypeReference, definition

		expect:
		type.type == result
		type.arrayDimensions == dimensions

		where:
		definition     | result               | dimensions
		"int"          | PrimitiveType.INT    | 0
		"bool[]"       | PrimitiveType.BOOL   | 1
		"string[][][]" | PrimitiveType.STRING | 3
	}

	@Unroll
	def "type chain #definition"() {
		def type = parse TypeChain, definition

		expect:
		collectElements(type) == elements
		type.arrayDimensions == chainDimensions
		collectDimensions(type) == dimensions

		where:
		definition                     | elements                   | chainDimensions | dimensions
		"int->int"                     | [INT, INT]                 | 0               | [0, 0]
		"(int->string)[]"              | [INT, STRING]              | 1               | [0, 0]
		"int->(int[]->void)->string[]" | [INT, [INT, VOID], STRING] | 0               | [0, [1, -1], 1]
	}

	def collectElements(TypeChain chain) {
		return chain.elements.collect {
			return it instanceof TypeChain ? collectElements(it) : it
		}
	}

	def collectDimensions(TypeChain chain) {
		return chain.elements.collect {
			if (it instanceof TypeChain) {
				return collectDimensions(it)
			} else if (it instanceof ArrayedTypeReference) {
				return it.arrayDimensions
			} else {
				return -1
			}
		}
	}

	protected <T extends TypeReference> T parse(Class<T> type, String definition) {
		def parserContext = ModuleDefinitionParser.createParser(new ModuleDefinitionSource("test", definition))
		def context = parserContext.parser.returnType()
		assert !parserContext.listener.inError
		def returnType = TypeParsers.parseReturnType(mockResolver(), context)
		assert type.isAssignableFrom(returnType.class)
		return (T) returnType
	}
}
