package com.prezi.spaghetti

import spock.lang.Specification

/**
 * Created by lptr on 16/05/14.
 */
class ReservedWordsTest extends Specification {
	def "protected words"() {
		expect:
		ReservedWords.PROTECTED_WORDS.asList() == [
		        "__module", "__spaghetti", "__consts"
		].sort()
	}
}
