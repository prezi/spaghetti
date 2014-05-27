package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.AbstractGenerator
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.definition.ModuleDefinition
import com.prezi.spaghetti.typescript.access.TypeScriptModuleAccessorGeneratorVisitor
import com.prezi.spaghetti.typescript.impl.TypeScriptModuleInitializerGeneratorVisitor
import com.prezi.spaghetti.typescript.impl.TypeScriptModuleInterfaceGeneratorVisitor
import com.prezi.spaghetti.typescript.impl.TypeScriptModuleStaticProxyGeneratorVisitor

import static com.prezi.spaghetti.ReservedWords.CONFIG
import static com.prezi.spaghetti.ReservedWords.SPAGHETTI_MODULE_CONFIGURATION

/**
 * Created by lptr on 12/11/13.
 */
class TypeScriptGenerator extends AbstractGenerator {

	public static final String CREATE_MODULE_FUNCTION = "__createSpaghettiModule"

	private final ModuleConfiguration config

	TypeScriptGenerator(ModuleConfiguration config) {
		this.config = config
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
	protected String processModuleJavaScriptInternal(ModuleDefinition module, ModuleConfiguration config, String javaScript)
	{
		return \
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
	private void generateLocalModule(ModuleDefinition module, File outputDirectory)
	{
		def contents = "declare var ${CONFIG}:any;\n"
		contents += new TypeScriptModuleInterfaceGeneratorVisitor(module).processModule()
		contents += new TypeScriptDefinitionIteratorVisitor(module).processModule()
		contents += new TypeScriptModuleStaticProxyGeneratorVisitor(module).processModule()
		contents += new TypeScriptModuleInitializerGeneratorVisitor(module, config.directDependentModules).processModule()
		TypeScriptUtils.createSourceFile(module, "I${module.alias}", outputDirectory, contents)
	}

	private static void generateDependentModule(ModuleDefinition module, File outputDirectory, boolean directDependency) {
		def contents = "declare var ${CONFIG}:any;\n"
		if (directDependency) {
			contents += new TypeScriptModuleAccessorGeneratorVisitor(module).processModule()
		}
		contents += new TypeScriptDefinitionIteratorVisitor(module).processModule()
		TypeScriptUtils.createSourceFile(module, module.alias, outputDirectory, contents)
	}
}
