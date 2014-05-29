package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.ModuleVisitor
import com.prezi.spaghetti.ast.QualifiedNode
import com.prezi.spaghetti.ast.parser.InternalAstParserException
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lptr on 01/06/14.
 */
@SuppressWarnings(["GroovyAssignabilityCheck", "GrDeprecatedAPIUsage"])
class NodeSetTest extends Specification {
	private static FQName qn(String name) { return FQName.fromString(name) }

	@Unroll
	def "Key: #key / #value"() {
		expect:
		set.key(value) == key

		where:
		key              | value                           | set
		""               | new TestNode("")                | new DefaultNamedNodeSet<TestNode>("test")
		"lajos"          | new TestNode("lajos")           | new DefaultNamedNodeSet<TestNode>("test")
		qn("alma.lajos") | new TestQNode(qn("alma.lajos")) | new DefaultQualifiedNodeSet<TestQNode>("test")
	}

	@Unroll
	def "Contains and get: #value, #value2"() {
		def key = set.key(value)
		def key2 = set.key(value2)
		assert !set.contains(key)
		assert !set.contains(key2)
		assert !set.contains(value)
		assert !set.contains(value2)
		assert !set.contains((Object) value)
		assert !set.contains((Object) value2)
		assert set.get(key) == null
		assert set.get(key2) == null
		assert !set.iterator().hasNext()
		assert set.toArray() == []
		assert set.toArray(new Object[0]) == []
		assert !set.containsAll(value)
		assert !set.containsAll(value2)

		set.add value
		set.add value2

		expect:
		set.contains(key)
		set.contains(key2)
		set.contains(value)
		set.contains(value2)
		set.contains((Object) value)
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
		new TestNode("")                | new TestNode("2")                    | new DefaultNamedNodeSet<TestNode>("test")
		new TestNode("lajos")           | new TestNode("lajos2")               | new DefaultNamedNodeSet<TestNode>("test")
		new TestQNode(qn("alma.lajos")) | new TestQNode(qn("alma.lajos.bela")) | new DefaultQualifiedNodeSet<TestQNode>("test")
	}


	@Unroll
	def "Duplicate: #value (#key)"() {
		set.add value

		def exception = null
		try {
			set.add value
			assert false
		} catch (InternalAstParserException ex) {
			exception = ex
		}

		expect:
		exception instanceof InternalAstParserException
		exception.message == "Test with the same name already exists: " + key

		where:
		key          | value                           | set
		""           | new TestNode("")                | new DefaultNamedNodeSet<TestNode>("test")
		"lajos"      | new TestNode("lajos")           | new DefaultNamedNodeSet<TestNode>("test")
		"alma.lajos" | new TestQNode(qn("alma.lajos")) | new DefaultQualifiedNodeSet<TestQNode>("test")
	}


	private static class TestNode extends AbstractNamedNode {
		TestNode(String name) {
			super(name)
		}

		@Override
		def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
			throw new IllegalStateException("This won't work")
		}
	}

	private static class TestQNode extends AbstractNamedNode implements QualifiedNode {
		final FQName qualifiedName

		TestQNode(FQName qualifiedName) {
			super(qualifiedName.localName)
			this.qualifiedName = qualifiedName
		}

		@Override
		def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
			throw new IllegalStateException("This won't work")
		}
	}
}
