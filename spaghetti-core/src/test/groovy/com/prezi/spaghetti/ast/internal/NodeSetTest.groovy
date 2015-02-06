package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.QualifiedNode
import com.prezi.spaghetti.ast.internal.parser.InternalAstParserException
import spock.lang.Specification
import spock.lang.Unroll

@SuppressWarnings(["GroovyAssignabilityCheck"])
class NodeSetTest extends Specification {
	private static FQName qn(String name) { return FQName.fromString(name) }

	@Unroll
	def "Key: #key / #value"() {
		expect:
		set.key(value) == key

		where:
		key              | value                           | set
		""               | new TestNode("")                | NodeSets.<TestNode>newNamedNodeSet("test")
		"lajos"          | new TestNode("lajos")           | NodeSets.<TestNode>newNamedNodeSet("test")
		qn("alma.lajos") | new TestQNode(qn("alma.lajos")) | NodeSets.<TestQNode>newQualifiedNodeSet("test")
	}

	@Unroll
	def "Contains and get: #value, #value2"() {
		def key = set.key(value)
		def key2 = set.key(value2)
		assert !set.contains(key)
		assert !set.contains(key2)
		assert !set.contains(value)
		assert !set.contains(value2)
		//noinspection GrDeprecatedAPIUsage
		assert !set.contains((Object) value)
		//noinspection GrDeprecatedAPIUsage
		assert !set.contains((Object) value2)
		assert set.get(key) == null
		assert set.get(key2) == null
		assert !set.iterator().hasNext()
		assert set.toArray() == []
		assert set.toArray(new Object[0]) == []
		assert !set.containsAll(value)
		assert !set.containsAll(value2)

		set.addInternal value
		set.addInternal value2

		expect:
		set.contains(key)
		set.contains(key2)
		set.contains(value)
		set.contains(value2)
		//noinspection GrDeprecatedAPIUsage
		set.contains((Object) value)
		//noinspection GrDeprecatedAPIUsage
		set.contains((Object) value2)
		set.get(key) == value
		set.get(key2) == value2
		set.toArray() == [value, value2]
		set.toArray(new Object[0]) == [value, value2]
		set.containsAll(value, value2)

		def iterator = set.iterator()
		iterator.hasNext()
		iterator.next() == value
		iterator.next() == value2
		!iterator.hasNext()

		where:
		value                           | value2                               | set
		new TestNode("")                | new TestNode("2")                    | NodeSets.<TestNode>newNamedNodeSet("test")
		new TestNode("lajos")           | new TestNode("lajos2")               | NodeSets.<TestNode>newNamedNodeSet("test")
		new TestQNode(qn("alma.lajos")) | new TestQNode(qn("alma.lajos.bela")) | NodeSets.<TestQNode>newQualifiedNodeSet("test")
	}


	@Unroll
	def "Duplicate: #value (#key)"() {
		set.addInternal value

		when:
		set.addInternal value

		then:
		def ex = thrown InternalAstParserException
		ex.message == message

		where:
		message                                              | value                           | set
		"A(n) test with the same name already exists: "           | new TestNode("")                | NodeSets.newNamedNodeSet("test")
		"A(n) test with the same name already exists: lajos"      | new TestNode("lajos")           | NodeSets.newNamedNodeSet("test")
		"A(n) test with the same name already exists: alma.lajos" | new TestQNode(qn("alma.lajos")) | NodeSets.newQualifiedNodeSet("test")
	}

	def "public mutable api throws exception"() {
		when: set.clear()
		then: thrown UnsupportedOperationException

		when: set.add(value)
		then: thrown UnsupportedOperationException

		when: set.addAll([value])
		then: thrown UnsupportedOperationException

		when: set.remove(value)
		then: thrown UnsupportedOperationException

		when: set.removeAll([value])
		then: thrown UnsupportedOperationException

		when: set.retainAll([value])
		then: thrown UnsupportedOperationException

		when: set.iterator().remove()
		then: thrown UnsupportedOperationException

		where:
		value                           | set
		new TestNode("lajos")           | NodeSets.<TestNode>newNamedNodeSet("test")
		new TestQNode(qn("alma.lajos")) | NodeSets.<TestQNode>newQualifiedNodeSet("test")
	}


	private static class TestNode extends AbstractNamedNode {
		TestNode(String name) {
			super(null, name)
		}

		@Override
		protected
		def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
			throw new IllegalStateException("This won't work")
		}
	}

	private static class TestQNode extends AbstractNamedNode implements QualifiedNode {
		final FQName qualifiedName

		TestQNode(FQName qualifiedName) {
			super(null, qualifiedName.localName)
			this.qualifiedName = qualifiedName
		}

		@Override
		protected
		def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
			throw new IllegalStateException("This won't work")
		}
	}
}
