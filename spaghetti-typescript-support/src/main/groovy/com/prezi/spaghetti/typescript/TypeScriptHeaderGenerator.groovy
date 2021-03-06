package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.bundle.DefinitionLanguage
import com.prezi.spaghetti.definition.EntityWithModuleMetaData
import com.prezi.spaghetti.generator.AbstractHeaderGenerator
import com.prezi.spaghetti.generator.GeneratorParameters
import com.prezi.spaghetti.generator.GeneratorUtils
import com.prezi.spaghetti.typescript.access.TypeScriptModuleAccessorGeneratorVisitor
import com.prezi.spaghetti.typescript.impl.TypeScriptModuleInitializerGeneratorVisitor
import com.prezi.spaghetti.typescript.impl.TypeScriptModuleProxyGeneratorVisitor
import com.prezi.spaghetti.typescript.type.consts.TypeScriptDependentConstGeneratorVisitor
import com.prezi.spaghetti.typescript.type.enums.TypeScriptDependentEnumGeneratorVisitor
import com.prezi.spaghetti.typescript.type.enums.TypeScriptOpaqueEnumGeneratorVisitor

import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS

class TypeScriptHeaderGenerator extends AbstractHeaderGenerator {

	TypeScriptHeaderGenerator() {
		super("typescript")
	}

	@Override
	void generateHeaders(GeneratorParameters params, File outputDirectory) throws IOException {
		def config = params.moduleConfiguration
		def header = params.header
		copySpaghettiClass(outputDirectory)
		generateLocalModule(config.localModule, outputDirectory, header)
		(config.directDependentModules + config.lazyDependentModules).each { dependentModule ->
			generateDependentModule(dependentModule, outputDirectory, header, true)
		}
		config.lazyDependentModules.each { dependentModule ->
			generateLazyGetter(dependentModule, outputDirectory)
		}
		config.transitiveDependentModules.each { dependentModule ->
			generateDependentModule(dependentModule, outputDirectory, header, false)
		}
	}

	private static generateLazyGetter(EntityWithModuleMetaData<ModuleNode> module, File outputDirectory) {
		def file = new File(outputDirectory, module.entity.name.replace(".", "_") + "__loader_generated.d.ts")
		def accessorName = GeneratorUtils.createLazyModuleAccessorName(module.entity.name)
		file << """declare function ${accessorName}(): Promise<${module.entity.name}.LazyModule>;"""
	}

	/**
	 * Copies Spaghetti.ts to the generated source directory.
	 */
	private static void copySpaghettiClass(File outputDirectory) {
		new File(outputDirectory, "${SPAGHETTI_CLASS}.d.ts") << TypeScriptHeaderGenerator.class.getResourceAsStream("/${SPAGHETTI_CLASS}.d.ts")
	}

	/**
	 * Generates local module.
	 */
	private static void generateLocalModule(ModuleNode module, File outputDirectory, String header)
	{
		if (module.source.definitionLanguage == DefinitionLanguage.Spaghetti) {
			def contents = ""
			contents += TypeScriptDefinitionImportVisitor.collectImports(module)
			contents += new TypeScriptDefinitionIteratorVisitor(module.name).visit(module)
			contents += new TypeScriptModuleProxyGeneratorVisitor(module.name).visit(module)
			contents += new TypeScriptModuleInitializerGeneratorVisitor(module.name).visit(module)
			TypeScriptUtils.createSourceFile(
				header,
				module.alias + ".module.ts",
				outputDirectory,
				contents)

		} else if (module.source.definitionLanguage == DefinitionLanguage.TypeScript) {
			// Local module does not need to include the module.d.ts.
			// And namespace export is generated by TypeScriptJavaScriptBundleProcessor.
		} else {
			throw new RuntimeException("Unsupported definition language: " + module.source.definitionLanguage.name());
		}
	}

	private static void generateDependentModule(EntityWithModuleMetaData<ModuleNode> module, File outputDirectory, String header, boolean generateAccessor) {
		DefinitionLanguage moduleLang = module.entity.source.definitionLanguage
		if (moduleLang == DefinitionLanguage.Spaghetti) {
			generateSpaghettiDependentModule(module, outputDirectory, header, generateAccessor);
		} else if (moduleLang == DefinitionLanguage.TypeScript) {
			// For TypeScript module definitions, the generated header contains only the module def,
			// The module accessor is generated by TypeScriptJavaScriptBundleProcessor.
			writeTypeScriptDtsFile(module.entity, outputDirectory, header, module.entity.source.contents);
		} else {
			throw new RuntimeException("Unsupported definition language: " + moduleLang.name());
		}
	}

	private static void generateSpaghettiDependentModule(EntityWithModuleMetaData<ModuleNode> module, File outputDirectory, String header, boolean generateAccessor) {
		def contents = ""
		if (generateAccessor) {
			contents += TypeScriptDefinitionImportVisitor.collectImports(module.entity)
			contents += new TypeScriptModuleAccessorGeneratorVisitor(module.entity.name).visit(module.entity)
			contents += new TypeScriptDefinitionIteratorVisitor(module.entity.name) {
				@Override
				String visitEnumNode(EnumNode node) {
					return new TypeScriptDependentEnumGeneratorVisitor(module.entity.name).visit(node)
				}
				@Override
				String visitConstNode(ConstNode node) {
					return new TypeScriptDependentConstGeneratorVisitor(module.entity.name).visit(node)
				}
			}.visit(module.entity)
		} else {
			contents += TypeScriptDefinitionImportVisitor.collectImports(module.entity)
			contents += new TypeScriptDefinitionIteratorVisitor(module.entity.name) {
				@Override
				String visitEnumNode(EnumNode node) {
					return new TypeScriptOpaqueEnumGeneratorVisitor(module.entity.name).visit(node)
				}
				@Override
				String visitConstNode(ConstNode node) {
					return ""
				}
			}.visit(module.entity)
		}
		writeTypeScriptDtsFile(module.entity, outputDirectory, header, contents)
	}

	private static void writeTypeScriptDtsFile(ModuleNode module, File outputDirectory, String header, String content) {
		TypeScriptUtils.createSourceFile(header, module.name + ".d.ts", outputDirectory, content);
	}
}
