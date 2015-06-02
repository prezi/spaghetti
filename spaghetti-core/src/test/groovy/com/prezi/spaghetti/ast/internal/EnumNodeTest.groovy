package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.internal.parser.AstParserException

class EnumNodeTest extends AstSpecification {
	def "empty enum is allowed"() {
		expect:
		mockEnum([:]).normalizedValues == mockEnum([:]).values
	}

	def "enum with fully implicit values has assigned values by position"() {
		expect:
		mockEnum(
				alma:null,
				bela:null,
				geza:null
		).normalizedValues == mockEnum(
				alma:0,
				bela:1,
				geza:2
		).values
	}

	def "enum with fully explicit values overrides implicit values"() {
		expect:
		mockEnum(
				alma:1,
				bela:2,
				geza:3
		).normalizedValues == mockEnum(
				alma:1,
				bela:2,
				geza:3
		).values
	}

	def "explicit values can have arbitrary order"() {
		expect:
		mockEnum(
				alma:3,
				bela:7,
				geza:0
		).normalizedValues == mockEnum(
				alma:3,
				bela:7,
				geza:0
		).values
	}

	def "a mix of implicit and explicit values is not permitted"() {
		when:
		mockEnum(
				alma:null,
				bela:0,
				geza:1
		).normalizedValues
		then:
		def ex = thrown AstParserException
		ex.message == "Parse error in mockMixed implicit and explicit entries in enum test"
	}

	def "duplicate values are not permitted"() {
		when:
		mockEnum(
				alma:1,
				bela:1
		).normalizedValues
		then:
		def ex = thrown AstParserException
		ex.message == "Parse error in mockDuplicate value in enum test"
	}

	def mockEnum(Map<String, Integer> values) {
		def node = new DefaultEnumNode(mockLoc, FQName.fromString("test"))
		values.each { name, value ->
			node.values.addInternal(new DefaultEnumValueNode(mockLoc, name, value))
		}
		return node
	}
}
