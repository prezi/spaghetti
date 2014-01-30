package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle;
import com.prezi.spaghetti.Wrapper;
import com.prezi.spaghetti.SourceMap;

import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

class ObfuscateBundle extends AbstractBundleTask
{
	private static final List<String> s_protectedSymbols = ["define", "prototype", "__consts"];

	private enum With
	{
		Closure, UglifyJS
	};

	private With d_with;

	public void withClosure()
	{
		d_with = With.Closure;
	}

	public void withUglifyJS()
	{
		d_with = With.UglifyJS;
	}

	private List<String> uglifyJSCommandLine(Set<String> symbols, File uglifyFile, File mapJStoMinFile)
	{

		return ["uglifyjs", uglifyFile, "--compress", "--mangle",
				"--reserved=" + symbols.join(","),
				"--source-map=" + mapJStoMinFile,
				"--source-map-url="];
	}

	public ObfuscateBundle()
	{
		d_with = With.UglifyJS;
		inputFile = new File(project.buildDir, "spaghetti/module.zip");
		outputFile = new File(project.buildDir, "spaghetti/module_obf.zip");
	}

	@TaskAction
	void run()
	{

		def config = readConfig(definition.text);
		def modules = config.localModules + config.getDependentModules();
		def obfuscateDir = new File(project.buildDir, "obfuscate");
		obfuscateDir.mkdirs();
		Set<String> symbols = s_protectedSymbols + modules.collect{new SymbolCollectVisitor().visit(it.context)}.flatten();
		def mapJStoMin;
		def bundle = ModuleBundle.load(inputFile);

		// OBFUSCATE
		def compressedJS = new StringBuilder();
		switch (d_with)
		{
		case With.Closure: _: {
			def closureFile = new File(obfuscateDir, "closure.js");
			closureFile.delete();
			closureFile << bundle.bundledJavaScript << "\nvar __a = {};\n" + symbols.collect{
				"/** @expose */\n__a." + it + " = {};\n"
			}.join("");

			def sourceMapBuilder = new StringBuilder();
			Closure.compile(closureFile.toString(), compressedJS, "JStoMin.map", sourceMapBuilder);
			mapJStoMin = sourceMapBuilder.toString();
			break;
		}
		case With.UglifyJS: _: {
			def uglifyFile = new File(obfuscateDir, "uglify.js");
			def mapJStoMinFile = new File(obfuscateDir, "JStoMin.map");
			uglifyFile.delete();
			uglifyFile << bundle.bundledJavaScript;
			def cmdLine = uglifyJSCommandLine(symbols, uglifyFile, mapJStoMinFile);

			def process = cmdLine.execute();
			process.waitForProcessOutput(compressedJS, System.err);
			if (process.exitValue() != 0) {
				throw new RuntimeException("Obfuscation failed with exit code " + process.exitValue());
			}
			mapJStoMin = mapJStoMinFile.text;
			break;
		}
		default: throw new RuntimeException("Invalid d_with: " + d_with);
		};

		// SOURCEMAP
		def finalSourceMap;
		if (bundle.sourceMap != null)
		{
			finalSourceMap = SourceMap.compose(bundle.sourceMap, mapJStoMin, "module.map");
		}
		else
		{
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
