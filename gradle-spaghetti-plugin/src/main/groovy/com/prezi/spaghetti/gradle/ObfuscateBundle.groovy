package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle
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
		this.conventionMapping.inputFile = { new File(project.buildDir, "spaghetti/module.zip") }
		this.conventionMapping.outputFile = { new File(project.buildDir, "spaghetti/module_obf.zip") }
		this.closureExterns = []
	}

	@TaskAction
	void run()
	{
		def config = readConfig(getModuleDefinitions())
		def modules = config.localModules + config.dependentModules
		def obfuscateDir = new File(project.buildDir, "obfuscate");
		obfuscateDir.mkdirs();
		Set<String> symbols = protectedSymbols + modules.collect{new SymbolCollectVisitor().visit(it.context)}.flatten() + getAdditionalSymbols()
		def bundle = ModuleBundle.load(getInputFile())

		// OBFUSCATE
		def compressedJS = new StringBuilder();
		def closureFile = new File(obfuscateDir, "closure.js");
		closureFile.delete();
		closureFile << bundle.bundledJavaScript << "\nvar __a = {};\n" + symbols.collect{
			"/** @expose */\n__a." + it + " = {};\n"
		}.join("");

		def sourceMapBuilder = new StringBuilder();
		def closureRet = Closure.compile(closureFile.toString(), compressedJS, bundle.name, sourceMapBuilder, getClosureExterns())
		if (closureRet != 0) {
			throw new RuntimeException("Closure returned with exit code " + closureRet)
		}
		def mapJStoMin = sourceMapBuilder.toString();

		// SOURCEMAP
		def finalSourceMap;
		if (bundle.sourceMap != null) {
			finalSourceMap = SourceMap.compose(bundle.sourceMap, mapJStoMin, "module.map", this.nodeSourceMapRoot);
		} else {
			finalSourceMap = mapJStoMin;
		}

		finalSourceMap = SourceMap.relativizePaths(finalSourceMap, new URI(project.rootDir.toString()));

		// BUNDLE
		def obfBundle = new ModuleBundle(bundle.name, bundle.definition, bundle.version, bundle.source, compressedJS.toString(), finalSourceMap);
		obfBundle.save(getOutputFile())
	}

	Set<String> additionalSymbols = []
	String nodeSourceMapRoot = null;

	@InputFiles
	Set<File> getClosureExterns()
	{
		return this.closureExterns;
	}

	public void nodeSourceMapRoot(String sourceMapRoot) {
		this.nodeSourceMapRoot = sourceMapRoot
	}

	public void closureExtern(String... externName) {
		project.files(externName).each{this.closureExterns.add(it)}
	}
}
