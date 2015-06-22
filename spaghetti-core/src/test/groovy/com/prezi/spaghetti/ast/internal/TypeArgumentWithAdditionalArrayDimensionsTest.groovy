package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.ExternInterfaceNode
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.StructReference
import spock.lang.Unroll

class TypeArgumentWithAdditionalArrayDimensionsTest extends AstSpecification {
	// See https://github.com/prezi/spaghetti/issues/165
	@Unroll
	def "type arguments are copied with withAdditionalArrayDimensions() #node"() {
		def typeArg = Mock(TypeReferenceInternal)
		node.argumentsInternal.add(typeArg)
		def extended = node.withAdditionalArrayDimensions(addDimensions) as StructReference
		expect:
		extended.arrayDimensions == expected
		extended.arguments == [typeArg]

		where:
		node                                                                       | addDimensions | expected
		new DefaultInterfaceReference(mockLoc, Mock(InterfaceNode), 2)             | 3             | 5
		new DefaultExternInterfaceReference(mockLoc, Mock(ExternInterfaceNode), 2) | 3             | 5
		new DefaultStructReference(mockLoc, Mock(StructNode), 2)                   | 3             | 5
	}
}
