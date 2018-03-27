package com.prezi.spaghetti.gradle;

import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.generator.JavaScriptBundleProcessor;
import com.prezi.spaghetti.generator.internal.Generators;
import com.prezi.spaghetti.gradle.internal.AbstractBundleModuleTask;
import com.prezi.spaghetti.obfuscation.ModuleObfuscator;
import com.prezi.spaghetti.obfuscation.ObfuscationParameters;
import com.prezi.spaghetti.obfuscation.ObfuscationResult;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Callable;

public class ObfuscateModule extends AbstractBundleModuleTask implements NeedsTypeScriptCompilerSpaghettiTask {
	private final Set<String> additionalSymbols = Sets.newLinkedHashSet();
	private final Set<Object> closureExterns = Sets.newLinkedHashSet();
	private String compilationLevel = "advanced";
	private String closureTarget = "es5";
	private File workDir;
	private String nodeSourceMapRoot;
	private File tsCompilerPath;

	public ObfuscateModule() {
		this.getConventionMapping().map("workDir", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return new File(getProject().getBuildDir(), "spaghetti/obfuscation/work");
			}

		});
		this.getConventionMapping().map("outputDirectory", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return new File(getProject().getBuildDir(), "spaghetti/obfuscation/bundle");
			}

		});
	}

	@Override
	protected ModuleBundle createBundle(ModuleConfiguration config, String javaScript, String sourceMap, File resourceDir) throws IOException, InterruptedException {

		JavaScriptBundleProcessor processor = Generators.getService(JavaScriptBundleProcessor.class, getLanguage());
		ModuleObfuscator obfuscator = new ModuleObfuscator(processor.getProtectedSymbols());
		ObfuscationResult result = obfuscator.obfuscateModule(new ObfuscationParameters(
				config,
				config.getLocalModule(),
				javaScript,
				sourceMap,
				null,
				getNodeSourceMapRoot(),
				getClosureExterns(),
				getAdditionalSymbols(),
				getWorkDir(),
				getCompilerPath(),
				getLogger(),
				getCompilationLevel(),
				getClosureTarget()
		));
		return super.createBundle(config, result.javaScript, result.sourceMap, resourceDir);
	}

	@InputDirectory
	@Optional
	public File getCompilerPath() {
		return tsCompilerPath;
	}

	public void setCompilerPath(File compilerPath) {
		this.tsCompilerPath = compilerPath;
	}

	public File getWorkDir() {
		return workDir;
	}

	public void setWorkDir(Object workDir) {
		this.workDir = getProject().file(workDir);
	}

	@SuppressWarnings("UnusedDeclaration")
	public void workDir(String workDir) {
		setWorkDir(workDir);
	}

	@Input
	public String getCompilationLevel() { return compilationLevel; }

	public void setCompilationLevel(String compilationLevel) { this.compilationLevel = compilationLevel; }

	@Input
	public Set<String> getAdditionalSymbols() {
		return additionalSymbols;
	}

	@SuppressWarnings("UnusedDeclaration")
	public Boolean additionalSymbols(String... symbols) {
		return additionalSymbols.addAll(Arrays.asList((String[]) symbols));
	}

	@SuppressWarnings("UnusedDeclaration")
	public void closureExterns(Object... externs) {
		closureExterns.addAll(Arrays.asList(externs));
	}

	@SuppressWarnings("UnusedDeclaration")
	public void closureExtern(Object... externs) {
		closureExterns(externs);
	}

	@InputFiles
	public Set<File> getClosureExterns() {
		return getProject().files(this.closureExterns).getFiles();
	}

	@Input
	@Optional
	public String getNodeSourceMapRoot() {
		return nodeSourceMapRoot;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void nodeSourceMapRoot(String sourceMapRoot) {
		this.nodeSourceMapRoot = sourceMapRoot;
	}

	@Input
	public String getClosureTarget() {
		return closureTarget;
	}

	public void setClosureTarget(String closureTarget) {
		this.closureTarget = closureTarget;
	}
}
