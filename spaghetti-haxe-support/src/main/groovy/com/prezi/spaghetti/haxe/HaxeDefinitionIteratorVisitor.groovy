package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.grammar.ModuleVisitor
import org.antlr.v4.runtime.RuleContext
import org.antlr.v4.runtime.misc.NotNull
import org.antlr.v4.runtime.tree.ParseTreeVisitor

/**
 * Created by lptr on 20/11/13.
 */
class HaxeDefinitionIteratorVisitor extends AbstractModuleVisitor<Void> {

	private final File outputDirectory
	private final Closure<ModuleVisitor<String>> createTypeVisitor

	HaxeDefinitionIteratorVisitor(ModuleDefinition module, File outputDirectory,
						Closure<ModuleVisitor<String>> createTypeVisitor
	) {
		super(module)
		this.outputDirectory = outputDirectory
		this.createTypeVisitor = createTypeVisitor
	}

	private void createSourceFile(String name, RuleContext ctx, ParseTreeVisitor<String> visitor) {
		def contents = ctx.accept(visitor)
		HaxeUtils.createHaxeSourceFile(name, module.name, outputDirectory, contents)
	}

	@Override
	Void visitTypeDefinition(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
	{
		createSourceFile(ctx.name.text, ctx, createTypeVisitor())
		return null
	}

	@Override
	Void visitEnumDefinition(@NotNull @NotNull ModuleParser.EnumDefinitionContext ctx)
	{
		createSourceFile(ctx.name.text, ctx, new HaxeEnumGeneratorVisitor())
		return null
	}

	@Override
	Void visitStructDefinition(@NotNull @NotNull ModuleParser.StructDefinitionContext ctx)
	{
		createSourceFile(ctx.name.text, ctx, new HaxeStructGeneratorVisitor(module))
		return null
	}
}
