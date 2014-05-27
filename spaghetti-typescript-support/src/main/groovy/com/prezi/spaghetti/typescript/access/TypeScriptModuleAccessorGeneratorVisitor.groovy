package com.prezi.spaghetti.typescript.access

import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.definition.WithJavaDoc
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.typescript.AbstractTypeScriptGeneratorVisitor
import org.antlr.v4.runtime.misc.NotNull

import static com.prezi.spaghetti.ReservedWords.CONFIG
import static com.prezi.spaghetti.ReservedWords.INSTANCE
import static com.prezi.spaghetti.ReservedWords.MODULES
import static com.prezi.spaghetti.ReservedWords.STATIC

/**
 * Created by lptr on 22/05/14.
 */
class TypeScriptModuleAccessorGeneratorVisitor extends AbstractTypeScriptGeneratorVisitor {
	TypeScriptModuleAccessorGeneratorVisitor(ModuleDefinition module) {
		super(module)
	}

	@WithJavaDoc
	@Override
	String visitModuleDefinition(@NotNull @NotNull ModuleParser.ModuleDefinitionContext ctx)
	{
		return \
"""export class ${module.alias} {

	private static ${INSTANCE}:any = ${CONFIG}[\"${MODULES}\"][\"${module.name}\"][\"${INSTANCE}\"];
	private static ${STATIC}:any = ${CONFIG}[\"${MODULES}\"][\"${module.name}\"][\"${STATIC}\"];
${visitChildren(ctx)}
}
"""
	}

	@Override
	String visitTypeDefinition(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
	{
		// Do not generate code for types
		return ""
	}

	@WithJavaDoc
	@Override
	String visitModuleMethodDefinition(@NotNull ModuleParser.ModuleMethodDefinitionContext ctx) {
		return super.visitModuleMethodDefinition(ctx)
	}

	@Override
	protected String visitMethodDefinitionInternal(@NotNull @NotNull @NotNull @NotNull ModuleParser.MethodDefinitionContext ctx)
	{
		def returnType = ctx.returnTypeChain().accept(this)

		def typeParams = ctx.typeParameters()?.accept(this) ?: ""
		def params
		def callParams
		if (ctx.parameters) {
			params = ctx.parameters.accept(this)
			callParams = ctx.parameters.elements.collect { it.name.text }.join(", ")
		} else {
			params = ""
			callParams = ""
		}

		def isStatic = ((ModuleParser.ModuleMethodDefinitionContext) ctx.parent).isStatic
		def delegate = module.alias + "." + (isStatic ? STATIC : INSTANCE)

		return \
"""	${isStatic ? "static " : ""}${ctx.name.text}${typeParams}(${params}):${returnType} {
		${returnType == "void"?"":"return "}${delegate}.${ctx.name.text}(${callParams});
	}
"""
	}
}
