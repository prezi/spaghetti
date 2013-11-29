package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.grammar.ModuleVisitor
import org.antlr.v4.runtime.misc.NotNull

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

	@Override
	Void visitTypeDefinition(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
	{
		def contents = ctx.accept(createTypeVisitor())
		HaxeUtils.createHaxeSourceFile(ctx.name.text, module.name, outputDirectory, contents)
		return null
	}

	@Override
	Void visitEnumDefinition(@NotNull @NotNull ModuleParser.EnumDefinitionContext ctx)
	{
		def contents = ctx.accept(new HaxeEnumGeneratorVisitor())
		HaxeUtils.createHaxeSourceFile(ctx.name.text, module.name, outputDirectory, contents)
		return null
	}

}
