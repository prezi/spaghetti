package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.generator.AbstractGenerator
import com.prezi.spaghetti.generator.GeneratorParameters

import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS

class KotlinGenerator extends AbstractGenerator {

	private final String header
	private final ModuleConfiguration config

	KotlinGenerator(GeneratorParameters params) {
		super(params)
		this.header = params.header
		this.config = params.moduleConfiguration
	}

	@Override
	void generateHeaders(File outputDirectory) {
		copySpaghettiClass(outputDirectory)
		generateModuleInitializer(config.localModule, outputDirectory, header)
		generateModuleStaticProxy(config.localModule, outputDirectory, header)
		generateModuleTypes(config.localModule, outputDirectory, header)
		config.dependentModules.each { dependentModule ->
			generateModuleTypes(dependentModule, outputDirectory, header)
			generateModuleAccessor(dependentModule, outputDirectory, header)
		}
	}

	@Override
	public void generateStubs(File outputDirectory) throws IOException {
		config.allModules.each { module ->
			for (type in module.types) {
				if (type instanceof InterfaceNode) {
					def contents = "" // new KotlinInterfaceStubGeneratorVisitor().visit(type)
					KotlinUtils.createKotlinSourceFile(header, module.name, type.name + "Stub", outputDirectory, contents)
				}
			}
		}
	}

	@Override
	protected String processModuleJavaScriptInternal(ModuleNode module, String javaScript)
	{
		return javaScript
	}

	/**
	 * Copies Spaghetti.hx to the generated source directory.
	 */
	private static void copySpaghettiClass(File outputDirectory) {
		new File(outputDirectory, "${SPAGHETTI_CLASS}.kt") << KotlinGenerator.class.getResourceAsStream("/${SPAGHETTI_CLASS}.kt")
	}

	/**
	 * Generates static proxy.
	 */
	private static void generateModuleStaticProxy(ModuleNode module, File outputDirectory, String header)
	{
		def contents = "" // new KotlinModuleProxyGeneratorVisitor(module).visit(module)
		KotlinUtils.createKotlinSourceFile(header, module.name, "__${module.alias}Proxy", outputDirectory, contents)
	}

	/**
	 * Generates initializer for module.
	 */
	private static void generateModuleInitializer(ModuleNode module, File outputDirectory, String header)
	{
		def initializerName = "__" + module.alias + "Init"
		def initializerContents = "" // new KotlinModuleInitializerGeneratorVisitor().visit(module)
		KotlinUtils.createKotlinSourceFile(header, module.name, initializerName, outputDirectory, initializerContents)
	}

	/**
	 * Generates accessor class for module.
	 */
	private static void generateModuleAccessor(ModuleNode module, File outputDirectory, String header)
	{
		def contents = "" // new KotlinModuleAccessorGeneratorVisitor(module).visit(module)
		KotlinUtils.createKotlinSourceFile(header, module.name, module.alias, outputDirectory, contents)
	}

	/**
	 * Generates interfaces, enums, structs and constants defined in the module.
	 */
	private static void generateModuleTypes(ModuleNode module, File outputDirectory, String header)
	{
		new KotlinDefinitionIteratorVisitor(outputDirectory, header, module.name).visit(module)
	}
}
