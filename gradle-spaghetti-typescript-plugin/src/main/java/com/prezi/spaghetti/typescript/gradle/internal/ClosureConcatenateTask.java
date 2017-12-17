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

	private Set<Map.Entry<String, String>> getDependencies() throws IOException {
		Map<String, String> deps = Maps.newHashMap();
		ModuleConfiguration config = readConfig(getDefinition());
		for (ModuleNode node : config.getAllDependentModules()) {
			deps.put(node.getAlias(), node.getAlias());
		}
		deps.putAll(getExternalDependencies());
		return deps.entrySet();
	}

	@TaskAction
	public void concat() throws IOException, InterruptedException {
		File workDir = getWorkDir();
		FileUtils.deleteQuietly(workDir);

		File jsFilesDir = new File(workDir, "js");
		File nodeDir = new File(jsFilesDir, "node_modules");
		FileUtils.forceMkdir(nodeDir);

		FileUtils.copyDirectory(getSourceDir(), jsFilesDir);

		for (Map.Entry<String, String> extern : getDependencies()) {
			String varName = extern.getKey();
			String importName = extern.getValue();
			FileUtils.write(
				new File(nodeDir, importName + ".js"),
				String.format("module.exports = %s;\n", varName));
		}

		File variableRenameMap = new File(workDir, "rename-map.txt");
		int exitValue = ClosureCompiler.concat(
			workDir,
			getOutputFile(),
			FileUtils.listFiles(jsFilesDir, new String[] {"js"}, true),
			Sets.<File>newHashSet(),
			CompilationLevel.SIMPLE,
			variableRenameMap);

		if (exitValue != 0) {
			throw new RuntimeException("Closure Compiler return an error code: " + exitValue);
		}

		String entryVarName = getEntryPointVarName(jsFilesDir, variableRenameMap);
		String line = String.format("\nvar __spaghettiMainModule=%s.default;\n", entryVarName);
		Files.append(line, getOutputFile(), Charsets.UTF_8);
	}

	private String getEntryPointVarName(File sourceDir, File variableRenameMap) throws IOException {
		File file = Iterables.getOnlyElement(
			FileUtils.listFiles(
				sourceDir,
				new NameFileFilter(getEntryPoint()),
				TrueFileFilter.INSTANCE));
		String closureModule = FilenameUtils.removeExtension(file.getAbsolutePath());
		closureModule = closureModule.replace("/", "$").replace(".", "_").replace("-", "_");

		String entryPointKey = "module" + closureModule + ":";
		Collection<String> lines = Files.asCharSource(variableRenameMap, Charsets.UTF_8).readLines();
		for (String line : lines) {
			if (line.startsWith(entryPointKey)) {
				return line.substring(entryPointKey.length());
			}
		}

		System.out.println(
			String.format(
				"Searching for: '%s' in: '%s'",
				entryPointKey,
				variableRenameMap.getAbsolutePath()));
		throw new RuntimeException("cannot find entry point in variable_renaming_report");
	}
}
