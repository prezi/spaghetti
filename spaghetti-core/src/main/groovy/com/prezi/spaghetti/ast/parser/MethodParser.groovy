package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.internal.DefaultMethodParameterNode
import com.prezi.spaghetti.ast.internal.DefaultTypeParameterNode
import com.prezi.spaghetti.ast.internal.MutableMethodNode
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.tree.TerminalNode

class MethodParser {
	static protected <T extends MutableMethodNode> T parseMethodDefinition(TypeResolver resolver, ModuleParser.MethodDefinitionContext methodCtx, T methodNode) {
		methodCtx.typeParameters()?.Name()?.each { TerminalNode name ->
			methodNode.typeParameters.add new DefaultTypeParameterNode(name.text), name
		}

		// Let further processing access type parameters as defined types
		resolver = new SimpleNamedTypeResolver(resolver, methodNode.typeParameters)

		methodNode.returnType = TypeParsers.parseReturnType(resolver, methodCtx.returnType())
		methodCtx.methodParameters()?.methodParameter()?.each { ModuleParser.MethodParameterContext paramCtx ->
			def pairCtx = paramCtx.typeNamePair()
			def name = pairCtx.Name().text
			def typeCtx = pairCtx.complexType()
			def type = TypeParsers.parseComplexType(resolver, typeCtx)

			def paramNode = new DefaultMethodParameterNode(name, type)
			AnnotationsParser.parseAnnotations(paramCtx.annotations(), paramNode)
			methodNode.parameters.add paramNode, paramCtx
		}
		return methodNode
	}
}
