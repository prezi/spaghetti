package com.prezi.spaghetti

import spock.lang.Specification

class ReservedWordsTest extends Specification {
	def "protected words"() {
		expect:
		ReservedWords.PROTECTED_WORDS.asList() == [
		        "__spaghetti", "getName", "getResourceUrl"
		].sort()
	}
}
