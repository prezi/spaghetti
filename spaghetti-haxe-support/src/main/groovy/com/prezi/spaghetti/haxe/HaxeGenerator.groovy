package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.config.ModuleConfiguration
import com.prezi.spaghetti.generator.AbstractGenerator
import com.prezi.spaghetti.haxe.access.HaxeModuleAccessorGeneratorVisitor
import com.prezi.spaghetti.haxe.impl.HaxeModuleInitializerGeneratorVisitor
import com.prezi.spaghetti.haxe.impl.HaxeModuleProxyGeneratorVisitor
import com.prezi.spaghetti.haxe.stub.HaxeInterfaceStubGeneratorVisitor

import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS

class HaxeGenerator extends AbstractGenerator {

	// Workaround variable to trick Haxe into exposing the module
	public static final String HAXE_MODULE_VAR = "__haxeModule"

	HaxeGenerator(ModuleConfiguration config) {
		super(config)
	}

	@Override
	void generateHeaders(File outputDirectory) {
		config.localModules.each { module ->
			copySpaghettiClass(outputDirectory)
			generateModuleInitializer(module, outputDirectory)
			generateModuleStaticProxy(module, outputDirectory)
			generateModuleTypes(module, outputDirectory)
		}
		config.allDependentModules.each { dependentModule ->
			generateModuleTypes(dependentModule, outputDirectory)
		}
		config.directDependentModules.each { dependentModule ->
			generateModuleAccessor(dependentModule, outputDirectory)
		}
	}

	@Override
	public void generateStubs(File outputDirectory) throws IOException {
		config.allModules.each { module ->
			for (type in module.types) {
				if (type instanceof InterfaceNode) {
					def contents = new HaxeInterfaceStubGeneratorVisitor().visit(type)
					HaxeUtils.createHaxeSourceFile(module.name, type.name + "Stub", outputDirectory, contents)
				}
			}
		}
	}

	@Override
	protected String processModuleJavaScriptInternal(ModuleNode module, String javaScript)
	{
"""// Haxe expects either window or exports to be present
var exports = exports || {};
var ${HAXE_MODULE_VAR};
${javaScript}
return ${HAXE_MODULE_VAR};
"""
	}

	/**
	 * Copies Spaghetti.hx to the generated source directory.
	 */
	private static void copySpaghettiClass(File outputDirectory) {
		new File(outputDirectory, "${SPAGHETTI_CLASS}.hx") << HaxeGenerator.class.getResourceAsStream("/${SPAGHETTI_CLASS}.hx")
	}

	/**
	 * Generates static proxy.
	 */
	private static void generateModuleStaticProxy(ModuleNode module, File outputDirectory)
	{
		def contents = new HaxeModuleProxyGeneratorVisitor(module).visit(module)
		HaxeUtils.createHaxeSourceFile(module.name, "__${module.alias}Proxy", outputDirectory, contents)
	}

	/**
	 * Generates initializer for module.
	 */
	private static void generateModuleInitializer(ModuleNode module, File outputDirectory)
	{
		def initializerName = "__" + module.alias + "Init"
		def initializerContents = new HaxeModuleInitializerGeneratorVisitor().visit(module)
		HaxeUtils.createHaxeSourceFile(module.name, initializerName, outputDirectory, initializerContents)
	}

	/**
	 * Generates accessor class for module.
	 */
	private static void generateModuleAccessor(ModuleNode module, File outputDirectory)
	{
		def contents = new HaxeModuleAccessorGeneratorVisitor(module).visit(module)
		HaxeUtils.createHaxeSourceFile(module.name, module.alias, outputDirectory, contents)
	}

	/**
	 * Generates interfaces, enums, structs and constants defined in the module.
	 */
	private static void generateModuleTypes(ModuleNode module, File outputDirectory)
	{
		new HaxeDefinitionIteratorVisitor(outputDirectory, module.name).visit(module)
	}
}
