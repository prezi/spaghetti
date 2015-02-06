package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.ExternInterfaceNode
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.Location
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.StructReference
import com.prezi.spaghetti.ast.TypeReference
import spock.lang.Specification
import spock.lang.Unroll

class TypeArgumentWithAdditionalArrayDimensionsTest extends Specification {
	// See https://github.com/prezi/spaghetti/issues/165
	@Unroll
	def "type arguments are copied with withAdditionalArrayDimensions() #node"() {
		def typeArg = Mock(TypeReference)
		node.argumentsInternal.add(typeArg)
		def extended = node.withAdditionalArrayDimensions(addDimensions) as StructReference
		expect:
		extended.arrayDimensions == expected
		extended.arguments == [typeArg]

		where:
		node                                                                                 | addDimensions | expected
		new DefaultInterfaceReference(Location.INTERNAL, Mock(InterfaceNode), 2)             | 3             | 5
		new DefaultExternInterfaceReference(Location.INTERNAL, Mock(ExternInterfaceNode), 2) | 3             | 5
		new DefaultStructReference(Location.INTERNAL, Mock(StructNode), 2)                   | 3             | 5
	}
}
