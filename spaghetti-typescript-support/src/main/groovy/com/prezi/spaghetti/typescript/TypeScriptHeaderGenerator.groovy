package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.generator.AbstractHeaderGenerator
import com.prezi.spaghetti.generator.GeneratorParameters
import com.prezi.spaghetti.typescript.access.TypeScriptModuleAccessorGeneratorVisitor
import com.prezi.spaghetti.typescript.impl.TypeScriptModuleInitializerGeneratorVisitor
import com.prezi.spaghetti.typescript.impl.TypeScriptModuleProxyGeneratorVisitor

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
		config.allDependentModules.each { dependentModule ->
			generateDependentModule(dependentModule, outputDirectory, header)
		}
	}

	/**
	 * Copies Spaghetti.hx to the generated source directory.
	 */
	private static void copySpaghettiClass(File outputDirectory) {
		new File(outputDirectory, "${SPAGHETTI_CLASS}.ts") << TypeScriptHeaderGenerator.class.getResourceAsStream("/${SPAGHETTI_CLASS}.ts")
	}

	/**
	 * Generates local module.
	 */
	private static void generateLocalModule(ModuleNode module, File outputDirectory, String header)
	{
		def contents = ""
		contents += new TypeScriptDefinitionIteratorVisitor().visit(module)
		contents += new TypeScriptModuleProxyGeneratorVisitor(module).visit(module)
		contents += new TypeScriptModuleInitializerGeneratorVisitor().visit(module)
		TypeScriptUtils.createSourceFile(header, module, module.alias, outputDirectory, contents)
	}

	private static void generateDependentModule(ModuleNode module, File outputDirectory, String header) {
		def contents = "declare var ${SPAGHETTI_CLASS}:any;\n"
		contents += new TypeScriptModuleAccessorGeneratorVisitor(module).visit(module)
		contents += new TypeScriptDefinitionIteratorVisitor().visit(module)
		TypeScriptUtils.createSourceFile(header, module, module.alias, outputDirectory, contents)
	}
}
