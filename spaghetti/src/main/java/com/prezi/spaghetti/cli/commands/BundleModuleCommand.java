package com.prezi.spaghetti.cli.commands;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.bundle.ModuleBundleFactory;
import com.prezi.spaghetti.bundle.ModuleBundleParameters;
import com.prezi.spaghetti.cli.SpaghettiCliException;
import com.prezi.spaghetti.config.ModuleConfiguration;
import io.airlift.command.Command;
import io.airlift.command.Option;

import java.io.File;
import java.util.SortedSet;

@Command(name = "bundle", description = "Create a module bundle.")
public class BundleModuleCommand extends AbstractLanguageAwareCommand {
	@Option(name = {"-T", "--type"},
			description = "Output type: zip or directory")
	private String type;

	@Option(name = {"-s", "--source"},
			description = "JavaScript module source",
			required = true)
	private File sourceFile;

	@Option(name = {"-M", "--source-map"},
			description = "JavaScript module source map")
	private File sourceMapFile;

	@Option(name = {"--source-base-url"},
			description = "Source base URL of the VCS of the module")
	private String sourceBaseUrl;

	@Option(name = {"-o", "--output"},
			description = "Output directory of ZIP file")
	private File output;

	@Option(name = {"-V", "--version"},
			description = "Version of the module")
	private String version;

	@Option(name = {"-r", "--resources"},
			description = "Resources directory")
	private File resourcesDirectory;

	@Override
	public Integer call() throws Exception {
		OutputType type;
		if (Strings.isNullOrEmpty(this.type) || this.type.toUpperCase().equals("ZIP")) {
			type = OutputType.ZIP;
		} else if (this.type.toUpperCase().equals("DIR")){
			type = OutputType.DIR;
		} else {
			throw new SpaghettiCliException("Invalid bundle type: " + this.type);
		}

		ModuleConfiguration config = parseConfig();
		ModuleNode moduleNode = config.getLocalModules().first();

		String sourceMap;
		if (sourceMapFile != null) {
			sourceMap = Files.asCharSource(sourceMapFile, Charsets.UTF_8).read();
		} else {
			sourceMap = null;
		}

		String javaScript = Files.asCharSource(sourceFile, Charsets.UTF_8).read();
		String processedJavaScript = createGenerator(config).processModuleJavaScript(moduleNode, javaScript);

		SortedSet<String> dependentModules = Sets.newTreeSet();
		for (ModuleNode dependentModule : config.getAllDependentModules()) {
			dependentModules.add(dependentModule.getName());
		}

		ModuleBundleParameters params = new ModuleBundleParameters(
				moduleNode.getName(),
				moduleNode.getSource().getContents(),
				version,
				sourceBaseUrl,
				processedJavaScript,
				sourceMap,
				dependentModules,
				resourcesDirectory);

		switch (type) {
			case DIR:
				ModuleBundleFactory.createDirectory(output, params);
			case ZIP:
				ModuleBundleFactory.createZip(output, params);
		}
		return 0;
	}

	private static enum OutputType {
		DIR, ZIP
	}
}
