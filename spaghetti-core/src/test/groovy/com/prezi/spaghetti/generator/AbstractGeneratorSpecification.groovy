package com.prezi.spaghetti.generator

import com.google.common.collect.Sets
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.ModuleVisitorBase
import com.prezi.spaghetti.ast.ReferableTypeNode
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.TypeParameterNode
import com.prezi.spaghetti.ast.internal.NodeSets
import com.prezi.spaghetti.ast.internal.parser.AbstractModuleTypeParser
import com.prezi.spaghetti.ast.internal.parser.AstParserSpecification

class AbstractGeneratorSpecification extends AstParserSpecification {
	protected <T> T parseAndVisitNode(String fragment, ModuleVisitorBase<T> visitor, ReferableTypeNode[] existingTypes, Closure<AbstractModuleTypeParser> nodeParserCreator) {
		def locator = AstParserSpecification.mockLocator(fragment);
		def moduleParser = AstParserSpecification.parser(locator)
		def nodeParser = nodeParserCreator.call(locator, moduleParser)
		def resolvableTypes = [:]
		existingTypes.each { type ->
			resolvableTypes.put type.name, { type }
		}
		nodeParser.parse(mockResolver(resolvableTypes))
		return nodeParser.node.accept(visitor)
	}

	@SuppressWarnings("UnnecessaryQualifiedReference")
	protected StructNode mockStruct(String name, String qualifiedName = null, TypeParameterNode... typeParameters) {
		return Mock(StructNode) { StructNode it ->
			it.name >> name
			it.qualifiedName >> AbstractGeneratorSpecification.qName(qualifiedName, name)
			it.typeParameters >> NodeSets.newNamedNodeSet("type parameter", Sets.newLinkedHashSet(Arrays.asList(typeParameters)))
		}
	}

	@SuppressWarnings("UnnecessaryQualifiedReference")
	protected InterfaceNode mockInterface(String name, String qualifiedName = null, TypeParameterNode... typeParameters) {
		return Mock(InterfaceNode) { InterfaceNode it ->
			it.name >> name
			it.qualifiedName >> AbstractGeneratorSpecification.qName(qualifiedName, name)
			it.typeParameters >> NodeSets.newNamedNodeSet("type parameter", Sets.newLinkedHashSet(Arrays.asList(typeParameters)))
		}
	}

	protected TypeParameterNode mockTypeParameter(String name = null) {
		return Mock(TypeParameterNode) { TypeParameterNode it ->
			it.name >> name
			it.qualifiedName >> (name == null ? null : FQName.fromString(name))
		}
	}

	private static FQName qName(String qualifiedName, String name) {
		if (qualifiedName != null) {
			return FQName.fromString(qualifiedName)
		} else {
			return FQName.fromString("com.example.test", name)
		}
	}
}
