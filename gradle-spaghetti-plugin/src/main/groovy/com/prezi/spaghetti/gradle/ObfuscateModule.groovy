package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleObfuscator
import com.prezi.spaghetti.ObfuscationParameters
import com.prezi.spaghetti.Platforms
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional

class ObfuscateModule extends AbstractBundleModuleTask
{
	public ObfuscateModule()
	{
		this.conventionMapping.workDir = { new File(project.buildDir, "spaghetti/obfuscation/work") }
		this.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/obfuscation/bundle") }
	}

	@Override
	protected ModuleBundle createBundle(
			ModuleConfiguration config,
			ModuleDefinition module,
			String javaScript,
			String sourceMap,
			File resourceDir) {
		def obfuscator = new ModuleObfuscator(Platforms.getProtectedSymbols(getPlatform()))
		def result = obfuscator.obfuscateModule(new ObfuscationParameters(
				config: config,
				module: module,
				javaScript: javaScript,
				sourceMap: sourceMap,
				nodeSourceMapRoot: getNodeSourceMapRoot(),
				closureExterns: getClosureExterns(),
				additionalSymbols: getAdditionalSymbols(),
				workingDirectory: getWorkDir()
		))
		return super.createBundle(config, module, result.javaScript, result.sourceMap, resourceDir)
	}

	File workDir
	void workDir(String workDir) {
		this.workDir = project.file(workDir)
	}

	@Input
	Set<String> additionalSymbols = []
	public additionalSymbols(String... symbols) {
		additionalSymbols.addAll(symbols)
	}

	private final Set<Object> closureExterns = []
	public void closureExtern(Object... extern) {
		closureExterns.addAll(extern)
	}

	@InputFiles
	Set<File> getClosureExterns() {
		return project.files(this.closureExterns).files
	}

	@Input
	@Optional
	String nodeSourceMapRoot = null
	public void nodeSourceMapRoot(String sourceMapRoot) {
		this.nodeSourceMapRoot = sourceMapRoot
	}

}
