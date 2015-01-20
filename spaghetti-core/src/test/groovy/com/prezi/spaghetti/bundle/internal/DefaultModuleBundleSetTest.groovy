package com.prezi.spaghetti.bundle.internal

import com.prezi.spaghetti.bundle.ModuleBundle
import spock.lang.Specification

class DefaultModuleBundleSetTest extends Specification {
	def "empty"() {
		def set = new DefaultModuleBundleSet([] as Set, [] as Set)

		expect:
		set == [] as Set
		set.isEmpty()
		set.size() == 0
		set.directBundles == [] as Set
		set.transitiveBundles == [] as Set
	}

	def "mixed"() {
		def direct1 = createBundle("direct1")
		def direct2 = createBundle("direct2")
		def transitive1 = createBundle("transitive1")
		def transitive2 = createBundle("transitive2")

		def set = new DefaultModuleBundleSet([direct1, direct2] as Set, [transitive1, transitive2] as Set)

		expect:
		set == [direct1, direct2, transitive1, transitive2] as Set
		!set.isEmpty()
		set.size() == 4
		set.directBundles == [direct1, direct2] as Set
		set.transitiveBundles == [transitive1, transitive2] as Set
	}

	def "not disjoint"() {
		def bundle = createBundle("bundle")
		when:
		new DefaultModuleBundleSet([bundle] as Set, [bundle] as Set)

		then:
		thrown IllegalArgumentException
	}

	ModuleBundle createBundle(String name) {
		return Mock(ModuleBundle) {
			it.name >> name
			it.compareTo(_) >> { ModuleBundle other ->
				name.compareTo(other.name)
			}
		}
	}
}
