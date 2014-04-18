package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional

class ObfuscateBundle extends AbstractBundleModuleTask
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
		this.conventionMapping.outputFile = { new File(project.buildDir, "spaghetti/obfuscation/module_obf.zip") }
		this.closureExterns = []
	}

	@Override
	protected ModuleBundle createBundle(ModuleConfiguration config, ModuleDefinition module, String javaScript, String sourceMap, Set<File> resourceDirs) {
		def modules = config.localModules + config.dependentModules
		Set<String> symbols = protectedSymbols + modules.collect{new SymbolCollectVisitor().visit(it.context)}.flatten() + getAdditionalSymbols()

		def obfuscateDir = new File(project.buildDir, "spaghetti/obfuscation")
		obfuscateDir.delete() || obfuscateDir.deleteDir()
		obfuscateDir.mkdirs()

		// OBFUSCATE
		def compressedJS = new StringBuilder();
		def closureFile = new File(obfuscateDir, "closure.js");

		closureFile << javaScript << "\nvar __a = {}\n" + symbols.collect{
			"/** @expose */\n__a." + it + " = {}\n"
		}.join("");

		def sourceMapBuilder = new StringBuilder();
		def closureRet = Closure.compile(closureFile.toString(), compressedJS, module.name, sourceMapBuilder, getClosureExterns())
		if (closureRet != 0) {
			throw new RuntimeException("Closure returned with exit code " + closureRet)
		}
		def mapJStoMin = sourceMapBuilder.toString();

		// SOURCEMAP
		def finalSourceMap;
		if (sourceMap) {
			finalSourceMap = SourceMap.compose(sourceMap, mapJStoMin, "module.map", this.nodeSourceMapRoot)
		} else {
			finalSourceMap = mapJStoMin;
		}

		finalSourceMap = SourceMap.relativizePaths(finalSourceMap, new URI(project.rootDir.toString()));

		// BUNDLE
		return super.createBundle(config, module, compressedJS.toString(), finalSourceMap, resourceDirs)
	}

	@Input
	Set<String> additionalSymbols = []
	public additionalSymbols(String... symbols) {
		additionalSymbols.addAll(symbols)
	}

	@InputFiles
	Set<File> getClosureExterns()
	{
		return this.closureExterns;
	}

	@Input
	@Optional
	String nodeSourceMapRoot = null;
	public void nodeSourceMapRoot(String sourceMapRoot) {
		this.nodeSourceMapRoot = sourceMapRoot
	}

	public void closureExtern(String... externName) {
		project.files(externName).each{this.closureExterns.add(it)}
	}
}
