package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleObfuscator
import com.prezi.spaghetti.ObfuscationParameters
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional

class ObfuscateBundle extends AbstractBundleModuleTask
{
	private final Set<File> closureExterns;

	public ObfuscateBundle()
	{
		this.conventionMapping.workDir = { new File(project.buildDir, "spaghetti/obfuscation") }
		this.conventionMapping.outputFile = { new File(getWorkDir(), "module_obf.zip") }
		this.closureExterns = []
	}

	@Override
	protected ModuleBundle createBundle(ModuleConfiguration config, ModuleDefinition module, String javaScript, String sourceMap, Set<File> resourceDirs) {
		def result = ModuleObfuscator.obfuscateModule(new ObfuscationParameters(
				config: config,
				module: module,
				javaScript: javaScript,
				sourceMap: sourceMap,
				nodeSourceMapRoot: getNodeSourceMapRoot(),
				closureExterns: getClosureExterns(),
				additionalSymbols: getAdditionalSymbols(),
				workingDirectory: getWorkDir()
		))
		return super.createBundle(config, module, result.javaScript, result.sourceMap, resourceDirs)
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
