package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.FQName
import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 16/11/13.
 */
class HaxeInterfaceGeneratorVisitor extends AbstractHaxeMethodGeneratorVisitor {

	HaxeInterfaceGeneratorVisitor(ModuleDefinition module) {
		super(module)
	}

	@WithDeprecation
	@WithJavaDoc
	@Override
	String visitInterfaceDefinition(@NotNull @NotNull ModuleParser.InterfaceDefinitionContext ctx)
	{
		def interfaceTypeParams = ctx.typeParameters()?.parameters?.collect { param ->
			FQName.fromString(param.name.text)
		} ?: []
		return new HaxeInterfaceGeneratorVisitorInternal(module, interfaceTypeParams).visit(ctx)
	}

	private class HaxeInterfaceGeneratorVisitorInternal extends AbstractHaxeMethodGeneratorVisitor {

		private final Set<FQName> interfaceTypeParams

		protected HaxeInterfaceGeneratorVisitorInternal(ModuleDefinition module, List<FQName> interfaceTypeParams) {
			super(module)
			this.interfaceTypeParams = new LinkedHashSet<>(interfaceTypeParams)
		}

		@Override
		String visitInterfaceDefinition(@NotNull @NotNull ModuleParser.InterfaceDefinitionContext ctx)
		{
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
			def declaration = "interface ${typeName}"
			superTypes.each { superType ->
				declaration += " extends ${superType}"
			}
			return declaration + " {"
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
			if (interfaceTypeParams.contains(localTypeName))
			{
				return localTypeName
			}
			return super.resolveName(localTypeName)
		}
	}
}
