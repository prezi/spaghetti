package com.prezi.spaghetti.typescript.gradle.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.definition.DefinitionFile;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.gradle.internal.AbstractDefinitionAwareSpaghettiTask;
import com.prezi.spaghetti.gradle.internal.DefinitionAwareSpaghettiTask;
import com.prezi.spaghetti.gradle.internal.ExternalDependencyAwareTask;
import com.prezi.spaghetti.obfuscation.ObfuscationParameters;
import com.prezi.spaghetti.obfuscation.internal.ClosureCompiler;
import org.apache.commons.io.FileUtils;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClosureConcatenateTask extends AbstractDefinitionAwareSpaghettiTask implements ExternalDependencyAwareTask, DefinitionAwareSpaghettiTask {
	private File workDir;
	private File sourceRootDir;
	private FileTree sourceDir;
	private final Map<String, String> externalDependencies = Maps.newTreeMap();
	private final Set<String> nodeRequireDependencies = Sets.newHashSet();
	private final Set<File> npmPackageRoots = Sets.newHashSet();
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

	@Internal
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

	@Internal
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

	private static String commentOut(String line) {
		String prefix = "// ";
		return prefix + line.substring(prefix.length());
	}

	@TaskAction
	public void concat() throws IOException, InterruptedException {
		File workDir = getWorkDir();
		FileUtils.deleteQuietly(workDir);

		File nodeDir = new File(workDir, "node_modules");
		FileUtils.forceMkdir(nodeDir);

		getProject().copy(copySpec -> {
			copySpec.from(getSourceDir());
			copySpec.into(getWorkDir());
			copySpec.filter(line -> {
				if (line.startsWith("exports.") && line.endsWith("= void 0;")) {
					// Starting with typescript version 3.9 it emits lines like these
					// closure can't handle these, so these are commented out to preserve source maps
					// See https://github.com/microsoft/TypeScript/issues/41369 for more info
					return commentOut(line);
				}
				return line;
			});
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

		File closureOutputFile = new File(getWorkDir(), "output.js");

		int exitValue = ClosureCompiler.concat(
			workDir,
			closureOutputFile,
			relativeEntryPoint,
			Lists.newArrayList(relativeJsDir),
			Sets.newHashSet(),
			ObfuscationParameters.convertClosureTarget(getClosureTarget()));

		if (exitValue != 0) {
			throw new RuntimeException("Closure Compiler return an error code: " + exitValue);
		}

		List<String> outputLines = readLines(closureOutputFile.toPath());
		removeUnusedModuleConsts(outputLines);
		Files.write(getOutputFile().toPath(), outputLines);
	}

	private static void removeUnusedModuleConsts(List<String> outputLines) {
		Map<String, Integer> declarationMap = new HashMap<>();
		String pattern ="$$module$";
		Pattern declarationRegex = Pattern.compile("const ([^${:}]+"+Pattern.quote(pattern)+"[^ {:}]+) = [^{]*;");
		for (int i = 0; i < outputLines.size(); i++) {
			String line = outputLines.get(i);
			if (!line.startsWith("const ")) {
				continue;
			}
			if (!line.contains(pattern)) {
				continue;
			}

			System.out.println(i+" Line: "+line);
			Matcher matcher = declarationRegex.matcher(line);
			if (!matcher.matches()) {
				continue;
			}
			String name = matcher.group(1);
			System.out.println("Found: "+name);
			if (declarationMap.containsKey(name)) {
				throw new RuntimeException("Duplicated const: " + name);
			}
			declarationMap.put(name, i);
		}
		Map<String, Pattern> patternMap = declarationMap
			.entrySet()
			.stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> Pattern.compile("(^|\\W)" + Pattern.quote(entry.getKey()) + "(&|\\W)")
			));
		for (int i = 0; i < outputLines.size(); i++) {
			String line = outputLines.get(i);
			if (!line.contains(pattern)) {
				continue;
			}
			List<String> usedNames = new ArrayList<>();
			for (Map.Entry<String, Integer> entry : declarationMap.entrySet()) {
				String name = entry.getKey();
				Integer lineNumber = entry.getValue();
				if (i == lineNumber) {
					continue;
				}
				if (patternMap.get(name).matcher(line).find()) {
					System.out.println("Used: " + name);
					System.out.println("In line "+lineNumber+": " + line);
					usedNames.add(name);
				}
			}
			for(String name : usedNames) {
				declarationMap.remove(name);
			}
		}
		for (Map.Entry<String, Integer> entry : declarationMap.entrySet()) {
			String name = entry.getKey();
			Integer lineNumber = entry.getValue();
			System.out.println("Removed: " + name + "from line "+lineNumber);
			outputLines.set(lineNumber,commentOut(outputLines.get(lineNumber)));
		}
	}

	private static List<String> readLines(Path path) throws IOException {
		try(Stream<String> lines = Files.lines(path)) {
			return lines.collect(Collectors.toList());
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
