package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle;
import com.prezi.spaghetti.Wrapper;
import com.prezi.spaghetti.SourceMap;

import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

class ObfuscateBundle extends AbstractBundleTask
{
	private static final List<String> protectedSymbols = ["define",    // RequireJS
														  "prototype", // class definitions
														  "__consts"]; // Spaghetti constants

	public ObfuscateBundle()
	{
		inputFile = new File(project.buildDir, "spaghetti/module.zip");
		outputFile = new File(project.buildDir, "spaghetti/module_obf.zip");
	}

	@TaskAction
	void run()
	{
		def config = readConfig(definition);
		def modules = config.localModules + config.getDependentModules();
		def obfuscateDir = new File(project.buildDir, "obfuscate");
		obfuscateDir.mkdirs();
		Set<String> symbols = protectedSymbols + modules.collect{new SymbolCollectVisitor().visit(it.context)}.flatten();
		def bundle = ModuleBundle.load(inputFile);

		// OBFUSCATE
		def compressedJS = new StringBuilder();
		def closureFile = new File(obfuscateDir, "closure.js");
		closureFile.delete();
		closureFile << bundle.bundledJavaScript << "\nvar __a = {};\n" + symbols.collect{
			"/** @expose */\n__a." + it + " = {};\n"
		}.join("");

		def sourceMapBuilder = new StringBuilder();
		Closure.compile(closureFile.toString(), compressedJS, bundle.name.fullyQualifiedName, sourceMapBuilder);
		def mapJStoMin = sourceMapBuilder.toString();

		// SOURCEMAP
		def finalSourceMap;
		if (bundle.sourceMap != null) {
			finalSourceMap = SourceMap.compose(bundle.sourceMap, mapJStoMin, "module.map");
		} else {
			finalSourceMap = mapJStoMin;
		}

		// BUNDLE
		def obfBundle = new ModuleBundle(bundle.name, bundle.definition, bundle.version, bundle.source, compressedJS.toString(), finalSourceMap);
		obfBundle.save(outputFile);
	}

	@Override
	@InputFile
	File getDefinition()
	{
		return super.getDefinition()
	}
}
