package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.grammar.SpaghettiModuleParser
import com.prezi.spaghetti.grammar.SpaghettiModuleVisitor
import org.antlr.v4.runtime.misc.NotNull
/**
 * Created by lptr on 20/11/13.
 */
class HaxeEnumIteratorVisitor extends AbstractModuleVisitor<Void> {

	private final File outputDirectory
	private final Closure<SpaghettiModuleVisitor<String>> createVisitor
	private final Closure<String> getFileName

	HaxeEnumIteratorVisitor(ModuleDefinition module, File outputDirectory,
							 Closure<SpaghettiModuleVisitor<String>> createVisitor,
							 Closure<String> getFileName = null) {
		super(module)
		this.outputDirectory = outputDirectory
		this.createVisitor = createVisitor
		this.getFileName = getFileName ?: { SpaghettiModuleParser.EnumDefinitionContext ctx -> ctx.name.text }
	}

	@Override
	Void visitEnumDefinition(@NotNull @NotNull SpaghettiModuleParser.EnumDefinitionContext ctx)
	{
		def contents = createVisitor(ctx).visitEnumDefinition(ctx)
		def fileName = getFileName(ctx)
		HaxeUtils.createHaxeSourceFile(fileName, module.name, outputDirectory, contents)
		return null
	}
}
