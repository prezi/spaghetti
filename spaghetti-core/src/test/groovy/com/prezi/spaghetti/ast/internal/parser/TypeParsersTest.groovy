package com.prezi.spaghetti.ast.internal.parser

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.PrimitiveType
import com.prezi.spaghetti.ast.PrimitiveTypeReference
import com.prezi.spaghetti.ast.FunctionType
import com.prezi.spaghetti.ast.TypeReference
import com.prezi.spaghetti.definition.internal.ModuleDefinitionParser
import spock.lang.Unroll

import static com.prezi.spaghetti.ast.internal.PrimitiveTypeReferenceInternal.INT
import static com.prezi.spaghetti.ast.internal.PrimitiveTypeReferenceInternal.STRING
import static com.prezi.spaghetti.ast.internal.VoidTypeReferenceInternal.VOID

class TypeParsersTest extends AstSpecification {
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
		def type = parse FunctionType, definition

		expect:
		collectElements(type) == elements
		type.arrayDimensions == chainDimensions
		collectDimensions(type) == dimensions

		where:
		definition                           | elements                   | chainDimensions | dimensions
		"() -> int"                          | [VOID, INT]                | 0               | [0, 0]
		"(int) -> int"                       | [INT, INT]                 | 0               | [0, 0]
		"(int)->int"                         | [INT, INT]                 | 0               | [0, 0]
		"(int, string) -> int"               | [INT, STRING, INT]         | 0               | [0, 0, 0]
		"((int) -> string)[]"                | [INT, STRING]              | 1               | [0, 0]
		"(int, (int[]) -> void) -> string[]" | [INT, [INT, VOID], STRING] | 0               | [0, [1, 0], 1]
	}

	def collectElements(FunctionType chain) {
		return chain.elements.collect {
			return it instanceof FunctionType ? collectElements(it) : it
		}
	}

	def collectDimensions(FunctionType chain) {
		return chain.elements.collect {
			if (it instanceof FunctionType) {
				return collectDimensions(it)
			}
			return it.arrayDimensions
		}
	}

	protected <T extends TypeReference> T parse(Class<T> type, String definition) {
		def locator = mockLocator(definition)
		def parserContext = ModuleDefinitionParser.createParser(locator.source)
		def context = parserContext.parser.returnType()
		assert !parserContext.listener.inError
		def returnType = TypeParsers.parseReturnType(locator, mockResolver(), context)
		assert type.isAssignableFrom(returnType.class)
		return (T) returnType
	}
}
