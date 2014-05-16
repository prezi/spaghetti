package com.prezi.spaghetti

import com.prezi.spaghetti.definition.FQName
import spock.lang.Specification

/**
 * Created by lptr on 12/04/14.
 */
class FQNameTest extends Specification {
	def "invalids"() {
		when:
		FQName.fromString(what)

		then:
		thrown IllegalArgumentException

		where:
		what | _
		null | _
		""   | _
	}

	def "from string"() {
		def fqname = FQName.fromString(nameAsString)

		expect:
		fqname.namespace == namespace
		fqname.localName == localName
		fqname.fullyQualifiedName == nameAsString
		fqname.toString() == nameAsString

		where:
		nameAsString      | namespace   | localName
		"lajos"           | null        | "lajos"
		"bela.lajos"      | "bela"      | "lajos"
		"bela.geza.lajos" | "bela.geza" | "lajos"
	}

	def "from namespace and local name"() {
		def fqname = FQName.fromString(namespace, localName)

		expect:
		fqname.namespace == namespace
		fqname.localName == localName
		fqname.fullyQualifiedName == nameAsString
		fqname.toString() == nameAsString

		where:
		nameAsString      | namespace   | localName
		"lajos"           | null        | "lajos"
		"bela.lajos"      | "bela"      | "lajos"
		"bela.geza.lajos" | "bela.geza" | "lajos"
	}

	def "from empty namespace becomes null namepsace"() {
		def fqname = FQName.fromString("", "lajos")

		expect:
		fqname.namespace == null
		fqname.localName == "lajos"
		fqname.fullyQualifiedName == "lajos"
	}

	def "qualify name against another name"() {
		def parent = FQName.fromString(parentNameAsString)
		def child = FQName.fromString(nameAsString)
		def resolved = parent.qualifyLocalName(child)

		expect:
		resolved.fullyQualifiedName == resultName

		where:
		parentNameAsString | nameAsString | resultName
		"lajos.bela"       | "tibor.geza" | "tibor.geza"
		"lajos.bela"       | "geza"       | "lajos.geza"
	}


	def "qualify local name against namespace"() {
		def child = FQName.fromString(nameAsString)
		def resolved = FQName.qualifyLocalName(parentNamespace, child)

		expect:
		resolved.fullyQualifiedName == resultName

		where:
		parentNamespace | nameAsString | resultName
		"lajos.bela"    | "tibor.geza" | "tibor.geza"
		"lajos.bela"    | "geza"       | "lajos.bela.geza"
	}

	def "has namespace"() {
		def fqname = FQName.fromString(name)

		expect:
		fqname.hasNamespace() == hasNamespace

		where:
		name         | hasNamespace
		"lajos.bela" | true
		"lajos"      | false
	}

	def "compare to"() {
		def fqa = FQName.fromString(a)
		def fqb = FQName.fromString(b)

		expect:
		(fqa <=> fqb) == (a <=> b)

		where:
		a            | b
		"lajos"      | "lajos"
		"lajos"      | "bela"
		"lajos"      | "lajos.bela"
		"lajos.bela" | "bela"
		"lajos.bela" | "lajos.bela"
	}
}
