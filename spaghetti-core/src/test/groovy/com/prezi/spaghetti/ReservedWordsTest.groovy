package com.prezi.spaghetti

import spock.lang.Specification

class ReservedWordsTest extends Specification {
	def "protected words"() {
		expect:
		ReservedWords.PROTECTED_WORDS.asList() == [
		        "__instance", "__spaghetti", "__static", "getName", "getResourceUrl"
		].sort()
	}
}
