package com.prezi.spaghetti.generator

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.internal.DefaultEnumNode
import com.prezi.spaghetti.ast.internal.DefaultEnumValueNode
import com.prezi.spaghetti.ast.internal.parser.AstParserException

import static com.prezi.spaghetti.generator.EnumGeneratorUtils.calculateEnumValues

class EnumGeneratorUtilsTest extends AstSpecification {
	def "empty enum is allowed"() {
		expect:
		calculateEnumValues(mockEnum([:])) == [:]
	}

	def "enum with fully implicit values has assigned values by position"() {
		expect:
		calculateEnumValues(mockEnum(
		        alma:null,
				bela:null,
				geza:null
		)) == [
		        alma:0,
				bela:1,
				geza:2
		]
	}

	def "enum with fully explicit values overrides implicit values"() {
		expect:
		calculateEnumValues(mockEnum(
		        alma:1,
				bela:2,
				geza:3
		)) == [
		        alma:1,
				bela:2,
				geza:3
		]
	}

	def "explicit values can have arbitrary order"() {
		expect:
		calculateEnumValues(mockEnum(
		        alma:3,
				bela:7,
				geza:0
		)) == [
		        alma:3,
				bela:7,
				geza:0
		]
	}

	def "a mix of implicit and explicit values is not permitted"() {
		when:
		calculateEnumValues(mockEnum(
		        alma:null,
				bela:0,
				geza:1
		))
		then:
		def ex = thrown AstParserException
		ex.message == "Parse error in mockMixed implicit and explicit entries in enum test"
	}

	def "duplicate values are not permitted"() {
		when:
		calculateEnumValues(mockEnum(
				alma:1,
				bela:1
		))
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
