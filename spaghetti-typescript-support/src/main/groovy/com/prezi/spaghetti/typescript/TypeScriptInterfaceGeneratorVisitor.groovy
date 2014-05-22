package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.definition.FQName
import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptInterfaceGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {

	protected TypeScriptInterfaceGeneratorVisitor(ModuleDefinition module) {
		super(module)
	}

	@WithJavaDoc
	@Override
	String visitInterfaceDefinition(@NotNull @NotNull ModuleParser.InterfaceDefinitionContext ctx)
	{
		List<FQName> interfaceTypeParams = ctx.typeParameters()?.parameters?.collect { param ->
			FQName.fromString(param.name.text)
		} ?: []
		return new TypeScriptInterfaceGeneratorVisitorInternal(module, interfaceTypeParams).visit(ctx)
	}

	private class TypeScriptInterfaceGeneratorVisitorInternal extends AbstractTypeScriptGeneratorVisitor {

		private final Set<FQName> typeParams

		protected TypeScriptInterfaceGeneratorVisitorInternal(ModuleDefinition module, List<FQName> typeParams) {
			super(module)
			this.typeParams = new LinkedHashSet<>(typeParams)
		}

		@Override
		String visitInterfaceDefinition(@NotNull ModuleParser.InterfaceDefinitionContext ctx) {
			def typeName = ctx.name.text
			def typeParamsCtx = ctx.typeParameters()
			if (typeParamsCtx != null) {
				typeName += typeParamsCtx.accept(this)
			}

			def superTypes = ctx.superInterfaceDefinition()*.accept(this)
			def methodDefinitions = ctx.methodDefinition()*.accept(this)

			return \
"""${defineType(typeName, superTypes)}
${methodDefinitions.join("")}
}
"""
		}

		private static String defineType(String typeName, List<String> superTypes) {
			def declaration = "export interface ${typeName}"
			if (!superTypes.empty) {
				declaration += " extends ${superTypes.join(", ")}"
			}
			declaration += " {"
			return declaration
		}

		@Override
		String visitSuperInterfaceDefinition(@NotNull @NotNull ModuleParser.SuperInterfaceDefinitionContext ctx)
		{
			def superType = resolveName(FQName.fromContext(ctx.qualifiedName())).fullyQualifiedName
			superType += ctx.typeArguments()?.accept(this) ?: ""
			return superType
		}

		@Override
		protected FQName resolveName(FQName localTypeName)
		{
			if (typeParams.contains(localTypeName))
			{
				return localTypeName
			}
			return super.resolveName(localTypeName)
		}
	}
}
