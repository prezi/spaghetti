package com.prezi.spaghetti.typescript.gradle.internal;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.definition.DefinitionFile;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.gradle.internal.AbstractDefinitionAwareSpaghettiTask;
import com.prezi.spaghetti.gradle.internal.DefinitionAwareSpaghettiTask;
import com.prezi.spaghetti.gradle.internal.ExternalDependencyAwareTask;
import com.prezi.spaghetti.obfuscation.CompilationLevel;
import com.prezi.spaghetti.obfuscation.internal.ClosureCompiler;

public class ClosureConcatenateTask extends AbstractDefinitionAwareSpaghettiTask implements ExternalDependencyAwareTask, DefinitionAwareSpaghettiTask {
	private File workDir;
	private File sourceDir;
	private Map<String, String> externalDependencies = Maps.newTreeMap();
	private String entryPoint = null;
	private DefinitionFile definition = null;

	@Input
	public File getWorkDir() {
		return workDir;
	}

	public void setWorkDir(File dir) {
		workDir = dir;
	}

	@InputDirectory
	public File getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(File dir) {
		sourceDir = dir;
	}

	@OutputFile
	public File getOutputFile() {
		return new File(getWorkDir(), "concatenated.js");
	}

	@Input
	public Map<String, String> getExternalDependencies() {
		return externalDependencies;
	}
	public void externalDependencies(Map<String, String> externalDependencies) {
		this.externalDependencies.putAll(externalDependencies);
	}
	public void externalDependency(String importName, String dependencyName) {
		this.externalDependencies.put(importName, dependencyName);
	}
	public void externalDependency(String shorthand) {
		externalDependency(shorthand, shorthand);
	}

	@InputFile
	public File getDefinitionFile() {
		return getDefinition().getFile();
	}

	@Input
	public DefinitionFile getDefinition() {
		return definition;
	}

	public void setDefinition(DefinitionFile definition) {
		this.definition = definition;
	}

	@Input
	public String getEntryPoint() {
		return entryPoint;
	}

	public void setEntryPoint(String filename) {
		this.entryPoint = filename;
	}

	@TaskAction
	public void concat() throws IOException, InterruptedException {
		File workDir = getWorkDir();
		FileUtils.deleteQuietly(workDir);

		File jsFilesDir = new File(workDir, "js");
		File nodeDir = new File(jsFilesDir, "node_modules");
		FileUtils.forceMkdir(nodeDir);

		FileUtils.copyDirectory(getSourceDir(), jsFilesDir);
		ModuleConfiguration config = readConfig(getDefinition());

		for (Map.Entry<String, String> extern : getDependencies(config, getExternalDependencies())) {
			String varName = extern.getKey();
			String importName = extern.getValue();
			FileUtils.write(
				new File(nodeDir, importName + ".js"),
				String.format("module.exports = %s;\n", varName));
		}

		Collection<File> inputFiles = FileUtils.listFiles(jsFilesDir, new String[] {"js"}, true);
		File entryPointFile = filterFileList(inputFiles, getEntryPoint());
		File mainEntryPoint = createMainEntryPoint(workDir, entryPointFile, config.getLocalModule());
		inputFiles.add(mainEntryPoint);

		int exitValue = ClosureCompiler.concat(
			workDir,
			getOutputFile(),
			mainEntryPoint,
			inputFiles,
			Sets.<File>newHashSet(),
			CompilationLevel.SIMPLE);

		if (exitValue != 0) {
			throw new RuntimeException("Closure Compiler return an error code: " + exitValue);
		}
	}

	private File createMainEntryPoint(File workDir, File entryPoint, ModuleNode module) throws IOException {
		File file = new File(workDir, "_spaghetti-main.js");
		String data = String.format(
			"%s=require('%s');",
			module.getName(),
			entryPoint.getAbsolutePath());
		FileUtils.write(file, data);
		return file;
	}

	private static File filterFileList(Collection<File> list, String name) {
		for (File f: list) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		throw new RuntimeException("Cannot find entry point: " + name);
	}

	private static Set<Map.Entry<String, String>> getDependencies(ModuleConfiguration config, Map<String, String> externalDependencies) throws IOException {
		Map<String, String> deps = Maps.newHashMap();
		for (ModuleNode node : config.getAllDependentModules()) {
			String importName = node.getName().replace(".", "_");
			deps.put(node.getName(), importName);
		}
		deps.putAll(externalDependencies);
		return deps.entrySet();
	}
}
