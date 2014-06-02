package com.prezi.spaghetti.ast

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lptr on 12/04/14.
 */
class FQNameTest extends Specification {
	def "invalids"() {
		when:
		FQName.fromString(what)

		then:
		def ex = thrown IllegalArgumentException
		ex.message == "Qualified name cannot be empty"

		where:
		what | _
		null | _
		""   | _
	}

	@Unroll
	def "from string #nameAsString"() {
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

	@Unroll
	def "from namespace and local name: #nameAsString"() {
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

	@Unroll
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


	@Unroll
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

	@Unroll
	def "has namespace #name"() {
		def fqname = FQName.fromString(name)

		expect:
		fqname.hasNamespace() == hasNamespace

		where:
		name         | hasNamespace
		"lajos.bela" | true
		"lajos"      | false
	}

	@Unroll
	def "compare to #a <=> #b"() {
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

	@Unroll
	def "parts of #fqName == #parts"() {
		def name = FQName.fromString(fqName)

		expect:
		name.parts == parts

		where:
		fqName                  | parts
		"com.example.test.Test" | ["com", "example", "test", "Test"]
		"tibor"                 | ["tibor"]
	}
}
