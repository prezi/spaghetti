package com.prezi.spaghetti.generator

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.internal.DefaultEnumNode
import com.prezi.spaghetti.ast.internal.DefaultEnumValueNode
import com.prezi.spaghetti.ast.internal.parser.AstParserException
import spock.lang.Ignore

import static com.prezi.spaghetti.generator.EnumGeneratorUtils.calculateEnumValues

class EnumGeneratorUtilsTest extends AstSpecification {
	def "empty enum works"() {
		expect:
		calculateEnumValues(mockEnum([:])) == [:]
	}

	def "simple enum without values works"() {
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

	def "simple enum with partial values works"() {
		expect:
		calculateEnumValues(mockEnum(
		        alma:1,
				bela:null,
				geza:4
		)) == [
		        alma:1,
				bela:2,
				geza:4
		]
	}

	@Ignore("Let's figure out what to do here")
	def "backwards values work"() {
		expect:
		calculateEnumValues(mockEnum(
		        alma:3,
				bela:2,
				geza:1
		)) == [
		        alma:3,
				bela:2,
				geza:1
		]
	}

	@Ignore("Let's figure out what to do here")
	def "backwards with partial values work"() {
		expect:
		calculateEnumValues(mockEnum(
		        alma:3,
				bela:2,
				geza:1,
				lajos:null
		)) == [
		        alma:3,
				bela:2,
				geza:1,
				lajos:4
		]
	}

	def "simple enum with invalid values throws error"() {
		when:
		calculateEnumValues(mockEnum(
		        alma:null,
				bela:0,
				geza:1
		))
		then:
		def ex = thrown AstParserException
		ex.message == "Parse error in mockEnum value is wrong"
	}

	def mockEnum(Map<String, Integer> values) {
		def node = new DefaultEnumNode(mockLoc, FQName.fromString("test"))
		values.each { name, value ->
			node.values.addInternal(new DefaultEnumValueNode(mockLoc, name, value))
		}
		return node
	}
}
