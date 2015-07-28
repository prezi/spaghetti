package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.EnumValueNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.generator.AbstractHeaderGenerator
import com.prezi.spaghetti.generator.GeneratorParameters
import com.prezi.spaghetti.generator.GeneratorUtils
import com.prezi.spaghetti.kotlin.access.KotlinModuleAccessorGeneratorVisitor
import com.prezi.spaghetti.kotlin.impl.KotlinModuleInitializerGeneratorVisitor
import com.prezi.spaghetti.kotlin.impl.KotlinModuleProxyGeneratorVisitor

import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS

class KotlinHeaderGenerator extends AbstractHeaderGenerator {

	KotlinHeaderGenerator() {
		super("kotlin")
	}

	@Override
	void generateHeaders(GeneratorParameters params, File outputDirectory) throws IOException {
		def config = params.moduleConfiguration
		def header = params.header
		copySpaghettiClass(config.localModule, outputDirectory, header)
		generateModuleInitializer(config.localModule, outputDirectory, header)
		generateModuleStaticProxy(config.localModule, outputDirectory, header)
		generateModuleTypes(config.localModule, outputDirectory, header)
		config.allDependentModules.each { dependentModule ->
			generateForeignModuleTypes(dependentModule, outputDirectory, header)
		}
		config.directDependentModules.each { dependentModule ->
			generateModuleAccessor(dependentModule, outputDirectory, header)
		}
	}

	/**
	 * Copies Spaghetti.kt to the generated source directory.
	 */
	private static void copySpaghettiClass(ModuleNode module, File outputDirectory, String header) {
		def contents = KotlinHeaderGenerator.class.getResourceAsStream("/${SPAGHETTI_CLASS}.kt").text
		KotlinUtils.createKotlinSourceFile(header, module.name, SPAGHETTI_CLASS, outputDirectory, contents)
	}

	/**
	 * Generates static proxy.
	 */
	private static void generateModuleStaticProxy(ModuleNode module, File outputDirectory, String header)
	{
		def contents = new KotlinModuleProxyGeneratorVisitor().visit(module)
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
		def contents = new KotlinModuleAccessorGeneratorVisitor().visit(module)
		KotlinUtils.createKotlinSourceFile(header, module.name, module.alias, outputDirectory, contents)
	}

	/**
	 * Generates interfaces, enums, structs and constants defined in the module.
	 */
	private static void generateModuleTypes(ModuleNode module, File outputDirectory, String header)
	{
		new KotlinDefinitionIteratorVisitor(outputDirectory, header, module.name).visit(module)
	}

	/**
	 * Generates interfaces, enums, structs and constants defined in the foreign module.
	 */
	private static void generateForeignModuleTypes(ModuleNode module, File outputDirectory, String header) {
		new KotlinDefinitionIteratorVisitor(outputDirectory, header, module.name) {
			@Override
			KotlinEnumGeneratorVisitor createKotlinEnumGeneratorVisitor() {
				return new KotlinEnumGeneratorVisitor() {
					KotlinEnumGeneratorVisitor.EnumValueVisitor createEnumValueVisitor(String enumName) {
						return new KotlinEnumGeneratorVisitor.EnumValueVisitor(enumName) {
							@Override
							String generateValueExpression(EnumValueNode node) {
								return "js(\"${GeneratorUtils.createModuleAccessor(module).replaceAll("\"", "\\\\\"")}[\\\"${enumName}\\\"][\\\"${node.name}\\\"]\")"
							}
						}
					}
				}
			}
		}.visit(module)
	}
}
