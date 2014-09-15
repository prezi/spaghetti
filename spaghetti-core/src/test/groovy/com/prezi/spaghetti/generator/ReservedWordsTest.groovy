package com.prezi.spaghetti.generator

import spock.lang.Specification

class ReservedWordsTest extends Specification {
	def "protected words"() {
		expect:
		ReservedWords.PROTECTED_WORDS.asList() == [
		        "getModuleName", "getModuleVersion", "getSpaghettiVersion", "getResourceUrl"
		].sort()
	}
}
