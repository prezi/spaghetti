package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.AbstractGenerator
import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.config.ModuleConfiguration
import com.prezi.spaghetti.typescript.access.TypeScriptModuleAccessorGeneratorVisitor
import com.prezi.spaghetti.typescript.impl.TypeScriptModuleInitializerGeneratorVisitor
import com.prezi.spaghetti.typescript.impl.TypeScriptModuleInterfaceGeneratorVisitor
import com.prezi.spaghetti.typescript.impl.TypeScriptModuleStaticProxyGeneratorVisitor
import com.prezi.spaghetti.typescript.stub.TypeScriptInterfaceStubGeneratorVisitor

import static com.prezi.spaghetti.ReservedWords.CONFIG
import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_MODULE_CONFIGURATION

class TypeScriptGenerator extends AbstractGenerator {

	public static final String CREATE_MODULE_FUNCTION = "__createSpaghettiModule"

	TypeScriptGenerator(ModuleConfiguration config) {
		super(config)
	}

	@Override
	void generateHeaders(File outputDirectory) {
		config.localModules.each { module ->
			copySpaghettiClass(outputDirectory)
			generateLocalModule(module, outputDirectory)
		}
		config.directDependentModules.each { dependentModule ->
			generateDependentModule(dependentModule, outputDirectory, true)
		}
		config.transitiveDependentModules.each { dependentModule ->
			generateDependentModule(dependentModule, outputDirectory, false)
		}
	}

	@Override
	void generateStubs(File outputDirectory) throws IOException {
		config.allModules.each { module ->
			def contents = ""
			for (type in module.types) {
				if (type instanceof InterfaceNode) {
					contents += new TypeScriptInterfaceStubGeneratorVisitor().visit(type)
				}
			}
			TypeScriptUtils.createSourceFile(module, module.alias + "Stubs", outputDirectory, contents)
		}
	}

	@Override
	protected String processModuleJavaScriptInternal(ModuleNode module, String javaScript)
	{
"""${javaScript}
return ${module.name}.${CREATE_MODULE_FUNCTION}(${CONFIG});
"""
	}

	/**
	 * Copies Spaghetti.hx to the generated source directory.
	 */
	private static void copySpaghettiClass(File outputDirectory) {
		new File(outputDirectory, "${SPAGHETTI_MODULE_CONFIGURATION}.ts") << TypeScriptGenerator.class.getResourceAsStream("/${SPAGHETTI_MODULE_CONFIGURATION}.ts")
	}

	/**
	 * Generates local module.
	 */
	private void generateLocalModule(ModuleNode module, File outputDirectory)
	{
		def contents = "declare var ${CONFIG}:any;\n"
		contents += new TypeScriptModuleInterfaceGeneratorVisitor().visit(module)
		contents += new TypeScriptDefinitionIteratorVisitor().visit(module)
		contents += new TypeScriptModuleStaticProxyGeneratorVisitor(module).visit(module)
		contents += new TypeScriptModuleInitializerGeneratorVisitor(config.directDependentModules).visit(module)
		TypeScriptUtils.createSourceFile(module, "I${module.alias}", outputDirectory, contents)
	}

	private static void generateDependentModule(ModuleNode module, File outputDirectory, boolean directDependency) {
		def contents = "declare var ${CONFIG}:any;\n"
		if (directDependency) {
			contents += new TypeScriptModuleAccessorGeneratorVisitor(module).visit(module)
		}
		contents += new TypeScriptDefinitionIteratorVisitor().visit(module)
		TypeScriptUtils.createSourceFile(module, module.alias, outputDirectory, contents)
	}
}
