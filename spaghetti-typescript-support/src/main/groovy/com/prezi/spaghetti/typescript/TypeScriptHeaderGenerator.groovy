package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.EnumValueNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.generator.AbstractHeaderGenerator
import com.prezi.spaghetti.generator.GeneratorParameters
import com.prezi.spaghetti.generator.GeneratorUtils
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
		def contents = ""
		contents += new TypeScriptDefinitionIteratorVisitor().visit(module)
		contents += new TypeScriptModuleProxyGeneratorVisitor().visit(module)
		contents += new TypeScriptModuleInitializerGeneratorVisitor().visit(module)
		TypeScriptUtils.createSourceFile(header, module, module.alias, outputDirectory, contents)
	}

	private static void generateDependentModule(ModuleNode module, File outputDirectory, String header, boolean generateAccessor) {
		def contents = "declare var ${SPAGHETTI_CLASS}:any;\n"
		if (generateAccessor) {
			contents += new TypeScriptModuleAccessorGeneratorVisitor().visit(module)
			contents += new TypeScriptDefinitionIteratorVisitor() {
				@Override
				TypeScriptEnumGeneratorVisitor createTypeScriptEnumGeneratorVisitor() {
					return new TypeScriptEnumGeneratorVisitor() {
						TypeScriptEnumGeneratorVisitor.EnumValueVisitor createEnumValueVisitor(String enumName) {
							return new TypeScriptEnumGeneratorVisitor.EnumValueVisitor() {
								@Override
								String generateValueExpression(EnumValueNode node) {
									return "${GeneratorUtils.createModuleAccessor(module)}[\"${enumName}\"][\"${node.name}\"]"
								}
							}
						}
					}
				}
			}.visit(module)
		} else {
			contents += new TypeScriptDefinitionIteratorVisitor() {
				@Override
				TypeScriptEnumGeneratorVisitor createTypeScriptEnumGeneratorVisitor() {
					return new TypeScriptEnumGeneratorVisitor() {
						TypeScriptEnumGeneratorVisitor.EnumValueVisitor createEnumValueVisitor(String enumName) {
							return new TypeScriptEnumGeneratorVisitor.EnumValueVisitor() {
								@Override
								String visitEnumValueNode(EnumValueNode node) {
									return ""
								}
							}
						}
					}
				}
			}.visit(module)
		}
		TypeScriptUtils.createSourceFile(header, module, module.alias, outputDirectory, contents)
	}
}
