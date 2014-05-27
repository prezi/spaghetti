package com.prezi.spaghetti.packaging

import spock.lang.Specification

/**
 * Created by lptr on 25/05/14.
 */
class DependencyTreeResolverTest extends Specification {

	def "normal resolution"() {
		def processor = Mock(DependencyTreeResolver.DependencyProcessor)

		when:
		def instances = DependencyTreeResolver.resolveDependencies([
				c: ["a", "b"],
				a: ["b"],
				b: []
		], processor)

		then:
		1 * processor.processDependency("b", []) >> 0
		1 * processor.processDependency("a", [0]) >> 1
		1 * processor.processDependency("c", [1, 0]) >> 2
		instances == [
		        b: 0,
				a: 1,
				c: 2
		]
		0 * _
	}

	def "cyclic dependency 1"() {
		def processor = Mock(DependencyTreeResolver.DependencyProcessor)

		when:
		DependencyTreeResolver.resolveDependencies([
				c: ["a", "b"],
				a: ["b"],
				b: ["c"]
		], processor)

		then:
		0 * _
		thrown IllegalStateException
	}

	def "cyclic dependency 2"() {
		def processor = Mock(DependencyTreeResolver.DependencyProcessor)

		when:
		DependencyTreeResolver.resolveDependencies([
				c: ["a", "b"],
				a: ["b", "c"],
				b: []
		], processor)

		then:
		1 * processor.processDependency("b", [])
		0 * _
		thrown IllegalStateException
	}

	def "non-existent module"() {
		def processor = Mock(DependencyTreeResolver.DependencyProcessor)

		when:
		DependencyTreeResolver.resolveDependencies([
				c: ["a", "b"],
				a: ["b", "d"],
				b: []
		], processor)

		then:
		1 * processor.processDependency("b", [])
		0 * _
		thrown IllegalStateException
	}
}
