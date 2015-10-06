package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.StructNode;
import com.prezi.spaghetti.ast.StructReference;
import com.prezi.spaghetti.ast.TypeNode;
import com.prezi.spaghetti.ast.TypeReference;
import com.prezi.spaghetti.ast.internal.DefaultFQName;
import com.prezi.spaghetti.ast.internal.DefaultMethodNode;
import com.prezi.spaghetti.ast.internal.DefaultPropertyNode;
import com.prezi.spaghetti.ast.internal.DefaultStructNode;
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode;
import com.prezi.spaghetti.internal.grammar.ModuleParser;
import org.antlr.v4.runtime.tree.TerminalNode;

public class StructParser extends AbstractModuleTypeParser<ModuleParser.StructDefinitionContext, DefaultStructNode> {
	public StructParser(Locator locator, ModuleParser.StructDefinitionContext context, String moduleName) {
		super(locator, context, createNode(locator, context, moduleName));
	}

	private static DefaultStructNode createNode(Locator locator, ModuleParser.StructDefinitionContext context, String moduleName) {
		DefaultStructNode node = new DefaultStructNode(locator.locate(context.Name()), DefaultFQName.fromString(moduleName, context.Name().getText()));
		AnnotationsParser.parseAnnotations(locator, context.annotations(), node);
		DocumentationParser.parseDocumentation(locator, context.documentation, node);

		ModuleParser.TypeParametersContext typeParameters = context.typeParameters();
		if (typeParameters != null) {
			for (TerminalNode name : typeParameters.Name()) {
				node.getTypeParameters().add(new DefaultTypeParameterNode(locator.locate(name), name.getText()), context);
			}
		}

		return node;
	}

	@Override
	public void parseInternal(TypeResolver resolver) {
		// Let further processing access type parameters as defined types
		resolver = new SimpleNamedTypeResolver(resolver, node.getTypeParameters());

		ModuleParser.SuperTypeDefinitionContext superCtx = getContext().superTypeDefinition();
		if (superCtx != null) {
			node.setSuperStruct(parseSuperType(locator, resolver, superCtx));
		}

		for (ModuleParser.StructElementDefinitionContext elemCtx : getContext().structElementDefinition()) {
			if (elemCtx.propertyDefinition() != null) {
				ModuleParser.PropertyDefinitionContext propCtx = elemCtx.propertyDefinition();
				ModuleParser.TypeNamePairContext pairCtx = propCtx.typeNamePair();
				String name = pairCtx.Name().getText();
				TypeReference type = TypeParsers.parseComplexType(locator, resolver, pairCtx.complexType());
				boolean optional = pairCtx.optional != null;

				DefaultPropertyNode propertyNode = new DefaultPropertyNode(locator.locate(pairCtx.Name()), name, type, optional);
				AnnotationsParser.parseAnnotations(locator, propCtx.annotations(), propertyNode);
				DocumentationParser.parseDocumentation(locator, propCtx.documentation, propertyNode);
				node.getProperties().add(propertyNode, propCtx);
			} else if (elemCtx.methodDefinition() != null) {
				ModuleParser.MethodDefinitionContext methodCtx = elemCtx.methodDefinition();
				DefaultMethodNode methodNode = MethodParser.parseMethodDefinition(locator, resolver, methodCtx);
				node.getMethods().add(methodNode, methodCtx.Name());
			}
		}
	}

	private StructReference parseSuperType(Locator locator, TypeResolver resolver, ModuleParser.SuperTypeDefinitionContext superCtx) {
		TypeNode superType = resolver.resolveType(TypeResolutionContext.create(superCtx.qualifiedName()));
		if (!(superType instanceof StructNode)) {
			throw new InternalAstParserException(superCtx, "Only structs can be super structs");
		}
		return TypeParsers.parseStructReference(locator, resolver, superCtx, superCtx.typeArguments(), (StructNode) superType, 0);
	}
}
