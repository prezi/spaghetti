package com.prezi.spaghetti.typescript.gradle.internal;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.prezi.spaghetti.obfuscation.ObfuscationParameters;
import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
	private Set<String> nodeRequireDependencies = Sets.newHashSet();
	private Collection<File> entryPoints = null;
	private DefinitionFile definition = null;
	private String closureTarget = "es5";

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

	@Input
	public Set<String> getNodeRequireDependencies() {
		return nodeRequireDependencies;
	}
	public void nodeRequireDependency(String name) {
		this.nodeRequireDependencies.add(name);
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
	public Collection<File> getEntryPoints() {
		return entryPoints;
	}

	public void setEntryPoints(Collection<File> files) {
		this.entryPoints = files;
	}

	@Input
	public String getClosureTarget() {
		return closureTarget;
	}

	public void setClosureTarget(String closureTarget) {
		this.closureTarget = closureTarget;
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

		for (String name : getNodeRequireDependencies()) {
			FileUtils.write(
				new File(nodeDir, name + ".js"),
				String.format("var _require=require;\nmodule.exports = _require('%s');\n", name));
		}

		Collection<File> inputFiles = FileUtils.listFiles(jsFilesDir, new String[] {"js"}, true);
		Collection<File> entryPointFiles = filterFileList(inputFiles, getEntryPoints());
		File mainEntryPoint = new File(workDir, "_spaghetti-entry.js");
		ClosureUtils.writeMainEntryPoint(
			mainEntryPoint,
			entryPointFiles,
			config.getLocalModule().getName());
		inputFiles.add(mainEntryPoint);

		int exitValue = ClosureCompiler.concat(
			workDir,
			getOutputFile(),
			mainEntryPoint,
			inputFiles,
			Sets.<File>newHashSet(),
			ObfuscationParameters.convertClosureTarget(getClosureTarget()));

		if (exitValue != 0) {
			throw new RuntimeException("Closure Compiler return an error code: " + exitValue);
		}
	}

	private static Collection<File> filterFileList(Collection<File> list, Collection<File> fileNames) {
		Set<String> names = Sets.newHashSet();
		for (File f: fileNames) {
			names.add(f.getName().replaceAll("(\\.d)?\\.tsx?$", ".js"));
		}

		Collection<File> foundFiles = Lists.newArrayList();
		for (File f: list) {
			if (names.contains(f.getName())) {
				foundFiles.add(f);
			}
		}

		if (foundFiles.size() != fileNames.size()) {
			throw new RuntimeException("Cannot find entry points: " + Joiner.on(", ").join(names));
		}
		return foundFiles;
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
