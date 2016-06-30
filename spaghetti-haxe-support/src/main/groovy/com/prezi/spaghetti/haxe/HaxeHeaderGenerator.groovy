package com.prezi.spaghetti.haxe

import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.ModuleVisitorBase
import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.definition.EntityWithModuleMetaData
import com.prezi.spaghetti.generator.AbstractHeaderGenerator
import com.prezi.spaghetti.generator.GeneratorParameters
import com.prezi.spaghetti.haxe.access.HaxeModuleAccessorGeneratorVisitor
import com.prezi.spaghetti.haxe.type.consts.HaxeConstGeneratorVisitor
import com.prezi.spaghetti.haxe.type.consts.HaxeDependentConstGeneratorVisitor
import com.prezi.spaghetti.haxe.type.enums.HaxeDependentEnumGeneratorVisitor
import com.prezi.spaghetti.haxe.type.enums.HaxeOpaqueEnumGeneratorVisitor
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
		config.transitiveDependentModules.each { dependentModule ->
			generateTransitiveDependencyTypes(dependentModule.getEntity(), outputDirectory, header)
		}
		config.directDependentModules.each { dependentModule ->
			generateModuleAccessor(dependentModule.getEntity(), dependentModule.getFormat(), outputDirectory, header)
			generateDirectDependencyTypes(dependentModule, outputDirectory, header)
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
		def contents = new HaxeModuleProxyGeneratorVisitor().visit(module)
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
	private static void generateModuleAccessor(ModuleNode module, ModuleFormat format, File outputDirectory, String header)
	{
		def contents = new HaxeModuleAccessorGeneratorVisitor(format).visit(module)
		HaxeUtils.createHaxeSourceFile(header, module.name, module.alias, outputDirectory, contents)
	}

	/**
	 * Generates interfaces, enums, structs and constants defined in the module.
	 */
	private static void generateModuleTypes(ModuleNode module, File outputDirectory, String header)
	{
		new HaxeDefinitionIteratorVisitor(outputDirectory, header, module.name).visit(module)
	}

	/**
	 * Generates interfaces, enum proxies, structs and constants for the foreign module.
	 */
	private static void generateDirectDependencyTypes(EntityWithModuleMetaData<ModuleNode> module, File outputDirectory, String header)
	{
		// For direct dependencies, enum member and const entry access is by reference
		// to the original definition site.
		new HaxeDefinitionIteratorVisitor(outputDirectory, header, module.entity.name) {
			@Override
			ModuleVisitorBase<String> createHaxeEnumGeneratorVisitor() {
				return new HaxeDependentEnumGeneratorVisitor(module.entity.name, module.format) {}
			}
			@Override
			ModuleVisitorBase<String> createHaxeConstGeneratorVisitor() {
				new HaxeDependentConstGeneratorVisitor(module.entity.name, module.format)
			}
		}.visit(module.entity)
	}

	/**
	 * Generates interfaces, opaque enum types and structs for the foreign module.
	 */
	private static void generateTransitiveDependencyTypes(ModuleNode module, File outputDirectory, String header)
	{
		new HaxeDefinitionIteratorVisitor(outputDirectory, header, module.name) {
			// For transitive dependencies, we consider enums only as opaque types
			// without an option to access their members.
			@Override
			ModuleVisitorBase<String> createHaxeEnumGeneratorVisitor() {
				return new HaxeOpaqueEnumGeneratorVisitor() {}
			}
			// For transitive dependencies, const nodes are not generated.
			@Override
			Void visitConstNode(ConstNode node) {
				return null
			}
		}.visit(module)
	}
}
