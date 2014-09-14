package com.prezi.spaghetti.ast.internal.parser

import org.antlr.v4.runtime.Token
import spock.lang.Specification

class PrimitivesTest extends Specification {
	def "ParseBool"() {
		when:
		def token = Mock(Token)
		token.text >> input

		then:
		Primitives.parseBoolean(token) == expected

		where:
		input   | expected
		null    | null
		"true"  | true
		"false" | false
	}

	def "ParseInt"() {
		when:
		def token = Mock(Token)
		token.text >> input

		then:
		Primitives.parseInt(token) == expected

		where:
		input    | expected
		null     | null
		"0"      | 0
		"-1"     | -1
		"12345"  | 12345
		"0x123"  | 0x123
		"-0x123" | -0x123
	}

	def "ParseDouble"() {
		when:
		def token = Mock(Token)
		token.text >> input

		then:
		Primitives.parseDouble(token) == expected

		where:
		input     | expected
		null      | null
		"0"       | 0
		"-1"      | -1
		"12345"   | 12345
		"0.1"     | 0.1
		".1"      | 0.1
		"-.1"     | -0.1
		"1.23e45" | 1.23e45
	}

	def "ParseString"() {
		when:
		def token = Mock(Token)
		token.text >> input

		then:
		Primitives.parseString(token) == expected

		where:
		input       | expected
		null        | null
		'"alma"'    | 'alma'
		'"alm\\"a"' | 'alm"a'
	}
}
