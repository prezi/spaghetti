package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.definition.AbstractModuleVisitor
import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleBaseVisitor
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
	private final boolean dependentModule
	private final Closure<ModuleVisitor<String>> createTypeVisitor

	HaxeDefinitionIteratorVisitor(ModuleDefinition module,
								  File outputDirectory,
								  boolean dependentModule,
								  Closure<ModuleVisitor<String>> createTypeVisitor
	) {
		super(module)
		this.outputDirectory = outputDirectory
		this.dependentModule = dependentModule
		this.createTypeVisitor = createTypeVisitor
	}

	private void createSourceFile(String name, RuleContext ctx, ParseTreeVisitor<String> visitor) {
		def contents = ctx.accept(visitor)
		HaxeUtils.createHaxeSourceFile(module, name, outputDirectory, contents)
	}

	@Override
	Void visitInterfaceDefinition(@NotNull @NotNull ModuleParser.InterfaceDefinitionContext ctx)
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

	@Override
	Void visitConstDefinition(@NotNull @NotNull ModuleParser.ConstDefinitionContext ctx)
	{
		ModuleBaseVisitor<String> constGenerator
		def fileName
		if (dependentModule) {
			fileName = ctx.name.text
			constGenerator = new HaxeConstProxyGeneratorVisitor(module)
		} else {
			fileName = "__" + ctx.name.text
			constGenerator = new HaxeConstGeneratorVisitor(module)
		}
		createSourceFile(fileName, ctx, constGenerator)
		return null
	}
}
