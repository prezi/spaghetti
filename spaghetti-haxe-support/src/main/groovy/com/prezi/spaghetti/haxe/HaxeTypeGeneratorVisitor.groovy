package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import com.prezi.spaghetti.grammar.SpaghettiModuleVisitor
import org.antlr.v4.runtime.misc.NotNull
/**
 * Created by lptr on 20/11/13.
 */
class HaxeTypeGeneratorVisitor extends AbstractModuleVisitor<Void> {

	private final File outputDirectory
	private final Closure<SpaghettiModuleVisitor<String>> createVisitor

	HaxeTypeGeneratorVisitor(ModuleDefinition module, File outputDirectory,
							 Closure<SpaghettiModuleVisitor<String>> createVisitor) {
		super(module)
		this.outputDirectory = outputDirectory
		this.createVisitor = createVisitor
	}

	@Override
	Void visitTypeDefinition(@NotNull @NotNull SpaghettiModuleParser.TypeDefinitionContext ctx)
	{
		def contents = createVisitor(ctx).visitTypeDefinition(ctx)
		HaxeUtils.createHaxeSourceFile(ctx.name.text, module.name, outputDirectory, contents)
		return null
	}
}
