package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.internal.DependencyTreeResolver
import spock.lang.Specification

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
		def ex = thrown IllegalStateException
		ex.message == "Cyclic dependency detected among modules: [a, b, c]"
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
		def ex = thrown IllegalStateException
		ex.message == "Cyclic dependency detected among modules: [a, c]"
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
		0 * _
		def ex = thrown IllegalArgumentException
		ex.message == "Module not found: d (dependency of module a)"
	}
}
