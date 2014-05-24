package com.prezi.spaghetti

import spock.lang.Specification

/**
 * Created by lptr on 16/05/14.
 */
class ReservedWordsTest extends Specification {
	def "protected words"() {
		expect:
		ReservedWords.PROTECTED_WORDS.asList() == [
		        "__instance", "__static", "__spaghetti", "__consts"
		].sort()
	}
}
