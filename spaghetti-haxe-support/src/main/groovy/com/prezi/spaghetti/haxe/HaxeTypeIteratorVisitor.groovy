package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.ModuleParser
import com.prezi.spaghetti.grammar.ModuleVisitor
import org.antlr.v4.runtime.misc.NotNull

/**
 * Created by lptr on 20/11/13.
 */
class HaxeTypeIteratorVisitor extends AbstractModuleVisitor<Void> {

	private final File outputDirectory
	private final Closure<ModuleVisitor<String>> createVisitor

	HaxeTypeIteratorVisitor(ModuleDefinition module, File outputDirectory,
							Closure<ModuleVisitor<String>> createVisitor) {
		super(module)
		this.outputDirectory = outputDirectory
		this.createVisitor = createVisitor
	}

	@Override
	Void visitTypeDefinition(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
	{
		def contents = createVisitor(ctx).visitTypeDefinition(ctx)
		HaxeUtils.createHaxeSourceFile(ctx.name.text, module.name, outputDirectory, contents)
		return null
	}
}
