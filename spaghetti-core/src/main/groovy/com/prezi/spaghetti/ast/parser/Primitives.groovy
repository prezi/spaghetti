package com.prezi.spaghetti.ast.parser

import groovy.json.StringEscapeUtils
import org.antlr.v4.runtime.Token

/**
 * Created by lptr on 29/05/14.
 */
class Primitives {

	static Integer parseInt(Token token) {
		def text = token?.text
		if (text) {
			int sign = 1
			if (text.startsWith("-")) {
				sign = -1
				text = text.substring(1)
			} else if (text.startsWith("+")) {
				text = text.substring(1)
			}
			def value
			if (text.startsWith("0x") || text.startsWith("0X")) {
				value = Integer.parseInt(text.substring(2), 16)
			} else {
				value = Integer.parseInt(text)
			}
			return sign * value
		} else {
			return null
		}
	}

	static Double parseDouble(Token token) {
		def text = token?.text
		if (text) {
			Double.parseDouble(text)
		} else {
			return null
		}
	}

	static Boolean parseBoolean(Token token) {
		def text = token?.text
		if (text) {
			return text == "true"
		} else {
			return null
		}
	}

	static String parseString(Token token) {
		def text = token?.text
		if (text) {
			def unescaped = StringEscapeUtils.unescapeJava(text);
			// strip surrounding quotes
			return unescaped[1..unescaped.size() - 2]
		} else {
			return null
		}
	}
}
