package com.prezi.spaghetti.generator

import com.google.common.collect.Sets
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.ModuleVisitorBase
import com.prezi.spaghetti.ast.ReferableTypeNode
import com.prezi.spaghetti.ast.StructNode
import com.prezi.spaghetti.ast.TypeNode
import com.prezi.spaghetti.ast.TypeParameterNode
import com.prezi.spaghetti.ast.internal.NodeSets
import com.prezi.spaghetti.ast.internal.parser.AbstractParser
import com.prezi.spaghetti.ast.internal.parser.AstParserSpecification
import com.prezi.spaghetti.ast.internal.parser.TypeResolutionContext
import com.prezi.spaghetti.ast.internal.parser.TypeResolver
import spock.lang.Specification

class AbstractGeneratorSpecification extends Specification {
	protected <T> T parseAndVisitNode(String fragment, ModuleVisitorBase<T> visitor, ReferableTypeNode[] existingTypes, Closure<AbstractParser> nodeParserCreator) {
		def locator = AstParserSpecification.mockLocator(fragment);
		def moduleParser = AstParserSpecification.parser(locator)
		def nodeParser = nodeParserCreator.call(locator, moduleParser)
		nodeParser.parse(mockResolver(existingTypes))
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

	private TypeResolver mockResolver(TypeNode... typeNodes) {
		def types = [:]
		typeNodes.each { types.put it.name, it}
		def resolver = Mock(TypeResolver)
		resolver.resolveType(_) >> { TypeResolutionContext context ->
			def name = context.name
			def type = types.get(name.fullyQualifiedName)
			if (type) {
				return type
			}

			throw new IllegalStateException("Ran out of scope while looking for type: ${name}")
		}
		return resolver
	}
}
