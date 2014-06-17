package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.InterfaceReference
import com.prezi.spaghetti.ast.internal.DefaultInterfaceMethodNode
import com.prezi.spaghetti.ast.internal.DefaultInterfaceNode
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.tree.TerminalNode

class InterfaceParser extends AbstractModuleTypeParser<ModuleParser.InterfaceDefinitionContext, InterfaceNode> {
	InterfaceParser(ModuleParser.InterfaceDefinitionContext context, String moduleName) {
		super(context, createNode(context, moduleName))
	}

	private static InterfaceNode createNode(ModuleParser.InterfaceDefinitionContext context, String moduleName) {
		def node = new DefaultInterfaceNode(FQName.fromString(moduleName, context.Name().text))
		AnnotationsParser.parseAnnotations(context.annotations(), node)
		DocumentationParser.parseDocumentation(context.documentation, node)
		context.typeParameters()?.Name()?.each { TerminalNode name ->
			node.typeParameters.add new DefaultTypeParameterNode(name.text), context
		}
		return node
	}

	@Override
	void parse(TypeResolver resolver) {
		// Let further processing access type parameters as defined types
		resolver = new SimpleNamedTypeResolver(resolver, node.typeParameters)

		context.superInterfaceDefinition().each { ModuleParser.SuperInterfaceDefinitionContext superCtx ->
			node.superInterfaces.add parseSuperInterface(resolver, superCtx)
		}

		context.interfaceMethodDefinition().each { ModuleParser.InterfaceMethodDefinitionContext methodCtx ->
			def nameCtx = methodCtx.methodDefinition().Name()
			def methodNode = new DefaultInterfaceMethodNode(nameCtx.text)
			AnnotationsParser.parseAnnotations(methodCtx.annotations(), methodNode)
			DocumentationParser.parseDocumentation(methodCtx.documentation, methodNode)
			MethodParser.parseMethodDefinition(resolver, methodCtx.methodDefinition(), methodNode)
			node.methods.add methodNode, nameCtx
		}
	}

	static protected InterfaceReference parseSuperInterface(TypeResolver resolver, ModuleParser.SuperInterfaceDefinitionContext superCtx) {
		def superType = resolver.resolveType(TypeResolutionContext.create(superCtx.qualifiedName()))
		if (!(superType instanceof InterfaceNode)) {
			throw new InternalAstParserException(superCtx, "Only interfaces can be super interfaces")
		}
		return TypeParsers.parseInterfaceReference(resolver, superCtx, superCtx.typeArguments(), superType, 0)
	}
}
