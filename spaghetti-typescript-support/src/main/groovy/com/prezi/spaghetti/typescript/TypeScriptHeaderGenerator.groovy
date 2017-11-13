package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ConstNode
import com.prezi.spaghetti.ast.EnumNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.ModuleVisitor
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
import com.prezi.spaghetti.typescript.type.enums.TypeScriptEnumGeneratorVisitor
import com.prezi.spaghetti.typescript.type.enums.TypeScriptOpaqueEnumGeneratorVisitor

import static com.prezi.spaghetti.generator.ReservedWords.SPAGHETTI_CLASS
import static com.prezi.spaghetti.typescript.TypeScriptJavaScriptBundleProcessor.CREATE_MODULE_FUNCTION

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
		config.directDependentModules.each { dependentModule ->
			generateDependentModule(dependentModule, outputDirectory, header, true)
		}
		config.transitiveDependentModules.each { dependentModule ->
			generateDependentModule(dependentModule, outputDirectory, header, false)
		}
	}

	/**
	 * Copies Spaghetti.ts to the generated source directory.
	 */
	private static void copySpaghettiClass(File outputDirectory) {
		new File(outputDirectory, "${SPAGHETTI_CLASS}.ts") << TypeScriptHeaderGenerator.class.getResourceAsStream("/${SPAGHETTI_CLASS}.ts")
	}

	/**
	 * Generates local module.
	 */
	private static void generateLocalModule(ModuleNode module, File outputDirectory, String header)
	{
		if (module.source.definitionLanguage == DefinitionLanguage.Spaghetti) {
			def contents = ""
			contents += new TypeScriptDefinitionIteratorVisitor().visit(module)
			contents += new TypeScriptModuleProxyGeneratorVisitor().visit(module)
			contents += new TypeScriptModuleInitializerGeneratorVisitor().visit(module)
			TypeScriptUtils.createSourceFile(header, module, module.alias, outputDirectory, contents)
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
			writeTypeScriptHeader(module.entity, outputDirectory, header);
		} else {
			throw new RuntimeException("Unsupported definition language: " + moduleLang.name());
		}
	}

	private static void generateSpaghettiDependentModule(EntityWithModuleMetaData<ModuleNode> module, File outputDirectory, String header, boolean generateAccessor) {
		def contents = ""
		if (generateAccessor) {
			contents += new TypeScriptModuleAccessorGeneratorVisitor(module.format).visit(module.entity)
			contents += new TypeScriptDefinitionIteratorVisitor() {
				@Override
				String visitEnumNode(EnumNode node) {
					return new TypeScriptDependentEnumGeneratorVisitor(module.entity.name, module.format).visit(node)
				}
				@Override
				String visitConstNode(ConstNode node) {
					return new TypeScriptDependentConstGeneratorVisitor(module.entity.name, module.format).visit(node)
				}
			}.visit(module.entity)
		} else {
			contents += new TypeScriptDefinitionIteratorVisitor() {
				@Override
				String visitEnumNode(EnumNode node) {
					return new TypeScriptOpaqueEnumGeneratorVisitor().visit(node)
				}
				@Override
				String visitConstNode(ConstNode node) {
					return ""
				}
			}.visit(module.entity)
		}
		TypeScriptUtils.createSourceFile(header, module.entity, module.entity.alias, outputDirectory, contents)
	}

	private static void writeTypeScriptHeader(ModuleNode module, File outputDirectory, String header) {
		TypeScriptUtils.createRawSourceFile(header, module.alias, outputDirectory, module.source.contents);
	}
}
