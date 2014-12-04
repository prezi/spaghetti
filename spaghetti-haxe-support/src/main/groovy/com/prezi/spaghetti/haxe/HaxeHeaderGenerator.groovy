package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.generator.AbstractHeaderGenerator
import com.prezi.spaghetti.generator.GeneratorParameters
import com.prezi.spaghetti.haxe.access.HaxeModuleAccessorGeneratorVisitor
import com.prezi.spaghetti.haxe.impl.HaxeModuleInitializerGeneratorVisitor
import com.prezi.spaghetti.haxe.impl.HaxeModuleProxyGeneratorVisitor

import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS

class HaxeHeaderGenerator extends AbstractHeaderGenerator {
	HaxeHeaderGenerator() {
		super("haxe")
	}

	@Override
	void generateHeaders(GeneratorParameters params, File outputDirectory) throws IOException {
		def config = params.moduleConfiguration
		def header = params.header
		copySpaghettiClass(outputDirectory)
		generateModuleInitializer(config.localModule, outputDirectory, header)
		generateModuleStaticProxy(config.localModule, outputDirectory, header)
		generateModuleTypes(config.localModule, outputDirectory, header)
		config.dependentModules.each { dependentModule ->
			generateModuleTypes(dependentModule, outputDirectory, header)
			generateModuleAccessor(dependentModule, outputDirectory, header)
		}
	}

	/**
	 * Copies Spaghetti.hx to the generated source directory.
	 */
	private static void copySpaghettiClass(File outputDirectory) {
		new File(outputDirectory, "${SPAGHETTI_CLASS}.hx") << HaxeHeaderGenerator.class.getResourceAsStream("/${SPAGHETTI_CLASS}.hx")
	}

	/**
	 * Generates static proxy.
	 */
	private static void generateModuleStaticProxy(ModuleNode module, File outputDirectory, String header)
	{
		def contents = new HaxeModuleProxyGeneratorVisitor(module).visit(module)
		HaxeUtils.createHaxeSourceFile(header, module.name, "__${module.alias}Proxy", outputDirectory, contents)
	}

	/**
	 * Generates initializer for module.
	 */
	private static void generateModuleInitializer(ModuleNode module, File outputDirectory, String header)
	{
		def initializerName = "__" + module.alias + "Init"
		def initializerContents = new HaxeModuleInitializerGeneratorVisitor().visit(module)
		HaxeUtils.createHaxeSourceFile(header, module.name, initializerName, outputDirectory, initializerContents)
	}

	/**
	 * Generates accessor class for module.
	 */
	private static void generateModuleAccessor(ModuleNode module, File outputDirectory, String header)
	{
		def contents = new HaxeModuleAccessorGeneratorVisitor(module).visit(module)
		HaxeUtils.createHaxeSourceFile(header, module.name, module.alias, outputDirectory, contents)
	}

	/**
	 * Generates interfaces, enums, structs and constants defined in the module.
	 */
	private static void generateModuleTypes(ModuleNode module, File outputDirectory, String header)
	{
		new HaxeDefinitionIteratorVisitor(outputDirectory, header, module.name).visit(module)
	}
}
