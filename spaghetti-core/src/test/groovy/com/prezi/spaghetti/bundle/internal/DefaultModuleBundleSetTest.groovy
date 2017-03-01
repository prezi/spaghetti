package com.prezi.spaghetti.bundle.internal

import com.google.common.collect.ImmutableSortedMap
import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.structure.internal.StructuredProcessor
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

	def "external dependency collection"() {
		def bundleA = createBundleWithExternalDependencies("A", ["Alpha": "alpha", "Beta": "beta"])
		def bundleB = createBundleWithExternalDependencies("B", ["Alphi": "alpha", "Gamma": "gamma"])

		when:
		def set = new DefaultModuleBundleSet([bundleA, bundleB], [] as Set)

		then:
		set.externalDependencies.get("alpha") == (["A", "B"] as SortedSet)
		set.externalDependencies.get("beta") == (["A"] as SortedSet)
		set.externalDependencies.get("zeta") == null
	}

	ModuleBundle createBundleWithExternalDependencies(String name, Map<String, String> extDeps) {
		new DefaultModuleBundle(
				Mock(StructuredProcessor),
				name,
				"1.0",
				ModuleFormat.UMD,
				"",
				[] as Set,
				extDeps,
				[] as Set)
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
