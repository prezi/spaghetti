package com.prezi.spaghetti.typescript.gradle.internal;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.prezi.spaghetti.obfuscation.ObfuscationParameters;
import org.apache.commons.io.FileUtils;
import org.gradle.api.Action;
import org.gradle.api.Transformer;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.definition.DefinitionFile;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.gradle.internal.AbstractDefinitionAwareSpaghettiTask;
import com.prezi.spaghetti.gradle.internal.DefinitionAwareSpaghettiTask;
import com.prezi.spaghetti.gradle.internal.ExternalDependencyAwareTask;
import com.prezi.spaghetti.obfuscation.internal.ClosureCompiler;

public class ClosureConcatenateTask extends AbstractDefinitionAwareSpaghettiTask implements ExternalDependencyAwareTask, DefinitionAwareSpaghettiTask {
	private File workDir;
	private File sourceRootDir;
	private FileTree sourceDir;
	private Map<String, String> externalDependencies = Maps.newTreeMap();
	private Set<String> nodeRequireDependencies = Sets.newHashSet();
	private Set<File> npmPackageRoots = Sets.newHashSet();
	private Collection<File> entryPoints = null;
	private DefinitionFile definition = null;
	private String closureTarget = "es5";

	@Override
	@Internal
	public ConfigurableFileCollection getDependentModules() {
		return super.getDependentModules();
	}

	@Override
	@Internal
	public ConfigurableFileCollection getLazyDependentModules() {
		return super.getLazyDependentModules();
	}

	@Input
	public File getWorkDir() {
		return workDir;
	}

	public void setWorkDir(File dir) {
		workDir = dir;
	}

	@InputFiles
	public FileTree getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(FileTree dir) {
		sourceDir = dir;
	}

	public File getSourceRootDir() {
		return sourceRootDir;
	}

	public void setSourceRootDir(File sourceRootDir) {
		this.sourceRootDir = sourceRootDir;
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

	@Input
	public Set<File> getNpmPackageRoots() {
		return npmPackageRoots;
	}
	public void includeNpmPackagesFrom(File folder) {
		this.npmPackageRoots.add(folder);
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

		File nodeDir = new File(workDir, "node_modules");
		FileUtils.forceMkdir(nodeDir);

		getProject().copy(new Action<CopySpec>() {
			@Override
			public void execute(CopySpec copySpec) {
				copySpec.from(getSourceDir());
				copySpec.into(getWorkDir());
				copySpec.filter(new Transformer<String, String>() {
					@Override
					public String transform(String s) {
						if (s.startsWith("exports.") && s.endsWith("= void 0;")) {
							// Starting with typescript version 3.9 it emits lines like these
							// closure can't handle these, so these have to be removed
							// See https://github.com/microsoft/TypeScript/issues/41369 for more info
							return null;
						}
						return s;
					}
				});
			}
		});
		ModuleConfiguration config = readConfig(getDefinition());

		ClosureCompiler.createExternAccessorsForConcat(nodeDir, config);
		ClosureCompiler.createExternAccessorsForConcat(nodeDir, getExternalDependencies());

		for (String name : getNodeRequireDependencies()) {
			FileUtils.write(
				new File(nodeDir, name + ".js"),
				String.format("var _require=require;\nmodule.exports = _require('%s');\n", name));
		}

		for (File root : getNpmPackageRoots()) {
			// Merge all the root dirs together into one.
			// Closure Compiler needs to see all the packages in one "node_modules" folder
			// to correctly resolve the imports.
			FileUtils.copyDirectory(root, nodeDir);
		}

		Collection<File> entryPointFiles = filterFileList(
			FileUtils.listFiles(workDir, new String[] {"js"}, true),
			getEntryPoints());
		File mainEntryPoint = new File(workDir, "_spaghetti-entry.js");
		ClosureCompiler.writeMainEntryPoint(
			mainEntryPoint,
			entryPointFiles,
			config.getLocalModule().getName());
		File relativeJsDir = new File(".");
		File relativeEntryPoint = new File(mainEntryPoint.getName());

		int exitValue = ClosureCompiler.concat(
			workDir,
			getOutputFile(),
			relativeEntryPoint,
			Lists.newArrayList(relativeJsDir),
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

		List<File> foundFiles = Lists.newArrayList();
		for (File f: list) {
			if (names.contains(f.getName())) {
				foundFiles.add(f);
			}
		}

		if (foundFiles.size() != fileNames.size()) {
			throw new RuntimeException("Cannot find entry points: " + Joiner.on(", ").join(names));
		}
		foundFiles.sort(Comparator.comparing(File::getAbsolutePath));
		return foundFiles;
	}
}
