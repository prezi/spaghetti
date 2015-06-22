package com.prezi.spaghetti.ast

import com.prezi.spaghetti.ast.internal.DefaultFQName
import spock.lang.Specification
import spock.lang.Unroll

class FQNameTest extends Specification {
	def "invalids"() {
		when:
		DefaultFQName.fromString(what)

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
		def fqname = DefaultFQName.fromString(nameAsString)

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
		def fqname = DefaultFQName.fromString(namespace, localName)

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
		def fqname = DefaultFQName.fromString("", "lajos")

		expect:
		fqname.namespace == null
		fqname.localName == "lajos"
		fqname.fullyQualifiedName == "lajos"
	}

	@Unroll
	def "qualify name against another name"() {
		def parent = DefaultFQName.fromString(parentNameAsString)
		def child = DefaultFQName.fromString(nameAsString)
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
		def child = DefaultFQName.fromString(nameAsString)
		def resolved = DefaultFQName.qualifyLocalName(parentNamespace, child)

		expect:
		resolved.fullyQualifiedName == resultName

		where:
		parentNamespace | nameAsString | resultName
		"lajos.bela"    | "tibor.geza" | "tibor.geza"
		"lajos.bela"    | "geza"       | "lajos.bela.geza"
	}

	@Unroll
	def "has namespace #name"() {
		def fqname = DefaultFQName.fromString(name)

		expect:
		fqname.hasNamespace() == hasNamespace

		where:
		name         | hasNamespace
		"lajos.bela" | true
		"lajos"      | false
	}

	@Unroll
	def "compare to #a <=> #b"() {
		def fqa = DefaultFQName.fromString(a)
		def fqb = DefaultFQName.fromString(b)

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
		def name = DefaultFQName.fromString(fqName)

		expect:
		name.parts == parts

		where:
		fqName                  | parts
		"com.example.test.Test" | ["com", "example", "test", "Test"]
		"tibor"                 | ["tibor"]
	}
}
