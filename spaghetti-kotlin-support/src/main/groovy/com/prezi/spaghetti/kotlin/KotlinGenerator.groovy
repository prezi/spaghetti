package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.generator.AbstractGenerator
import com.prezi.spaghetti.generator.GeneratorParameters
import com.prezi.spaghetti.kotlin.access.KotlinModuleAccessorGeneratorVisitor
import com.prezi.spaghetti.kotlin.impl.KotlinModuleInitializerGeneratorVisitor
import com.prezi.spaghetti.kotlin.impl.KotlinModuleProxyGeneratorVisitor

import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS

class KotlinGenerator extends AbstractGenerator {

	public static final String KOTLIN_MODULE_VAR = "__kotlinModule"

	private final String header
	private final ModuleConfiguration config

	KotlinGenerator(GeneratorParameters params) {
		super(params)
		this.header = params.header
		this.config = params.moduleConfiguration
	}

	@Override
	void generateHeaders(File outputDirectory) {
		copySpaghettiClass(config.localModule, outputDirectory)
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
"""
var ${KOTLIN_MODULE_VAR};
${javaScript}
return ${KOTLIN_MODULE_VAR};
"""
	}

	/**
	 * Copies Spaghetti.kt to the generated source directory.
	 */
	private void copySpaghettiClass(ModuleNode module, File outputDirectory) {
		def contents = KotlinGenerator.class.getResourceAsStream("/${SPAGHETTI_CLASS}.kt").text
		KotlinUtils.createKotlinSourceFile(header, module.name, SPAGHETTI_CLASS, outputDirectory, contents)
	}

	/**
	 * Generates static proxy.
	 */
	private static void generateModuleStaticProxy(ModuleNode module, File outputDirectory, String header)
	{
		def contents = new KotlinModuleProxyGeneratorVisitor(module).visit(module)
		KotlinUtils.createKotlinSourceFile(header, module.name, "__${module.alias}Proxy", outputDirectory, contents)
	}

	/**
	 * Generates initializer for module.
	 */
	private static void generateModuleInitializer(ModuleNode module, File outputDirectory, String header)
	{
		def initializerName = "__" + module.alias + "Init"
		def initializerContents = new KotlinModuleInitializerGeneratorVisitor().visit(module)
		KotlinUtils.createKotlinSourceFile(header, module.name, initializerName, outputDirectory, initializerContents)
	}

	/**
	 * Generates accessor class for module.
	 */
	private static void generateModuleAccessor(ModuleNode module, File outputDirectory, String header)
	{
		def contents = new KotlinModuleAccessorGeneratorVisitor(module).visit(module)
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
