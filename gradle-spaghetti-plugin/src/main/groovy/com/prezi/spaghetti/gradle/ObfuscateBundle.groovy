package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle;

import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

class ObfuscateBundle extends AbstractBundleTask
{
	private static final List<String> protectedSymbols = [
		// RequireJS
		"define",
		// class definitions
		"prototype",
		// Spaghetti constants
		"__consts",
		// Haxe class names -- Haxe likes to put this on global objects like Math and String and Date
		"__name__",
	]

	private final Set<File> closureExterns;

	public ObfuscateBundle()
	{
		inputFile = new File(project.buildDir, "spaghetti/module.zip");
		outputFile = new File(project.buildDir, "spaghetti/module_obf.zip");
		closureExterns = [];
	}

	@TaskAction
	void run()
	{
		def config = readConfig(definition);
		def modules = config.localModules + config.getDependentModules();
		def obfuscateDir = new File(project.buildDir, "obfuscate");
		obfuscateDir.mkdirs();
		Set<String> symbols = protectedSymbols + modules.collect{new SymbolCollectVisitor().visit(it.context)}.flatten() + additionalSymbols;
		def bundle = ModuleBundle.load(inputFile);

		// OBFUSCATE
		def compressedJS = new StringBuilder();
		def closureFile = new File(obfuscateDir, "closure.js");
		closureFile.delete();
		closureFile << bundle.bundledJavaScript << "\nvar __a = {};\n" + symbols.collect{
			"/** @expose */\n__a." + it + " = {};\n"
		}.join("");

		def sourceMapBuilder = new StringBuilder();
		Closure.compile(closureFile.toString(), compressedJS, bundle.name.fullyQualifiedName, sourceMapBuilder, closureExterns);
		def mapJStoMin = sourceMapBuilder.toString();

		// SOURCEMAP
		def finalSourceMap;
		if (bundle.sourceMap != null) {
			finalSourceMap = SourceMap.compose(bundle.sourceMap, mapJStoMin, "module.map");
		} else {
			finalSourceMap = mapJStoMin;
		}

		finalSourceMap = SourceMap.relativizePaths(finalSourceMap, new URI(project.rootDir.toString()));

		// BUNDLE
		def obfBundle = new ModuleBundle(bundle.name, bundle.definition, bundle.version, bundle.source, compressedJS.toString(), finalSourceMap);
		obfBundle.save(outputFile);
	}

	Set<String> additionalSymbols = []

	@Override
	@InputFile
	File getDefinition()
	{
		return super.getDefinition()
	}

	@InputFiles
	Set<File> getClosureExterns()
	{
		return closureExterns;
	}

	public void closureExtern(String... externName) {
		project.files(externName).each{closureExterns.add(it)}
	}
}
