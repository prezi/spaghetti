package com.prezi.spaghetti.cli.commands;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.bundle.ModuleBundleFactory;
import com.prezi.spaghetti.bundle.ModuleBundleParameters;
import com.prezi.spaghetti.cli.SpaghettiCliException;
import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.generator.Generators;
import com.prezi.spaghetti.generator.JavaScriptBundleProcessor;
import com.prezi.spaghetti.generator.JavaScriptBundleProcessorParameters;
import com.prezi.spaghetti.generator.internal.DefaultJavaScriptBundleProcessorParameters;
import com.prezi.spaghetti.generator.internal.InternalGeneratorUtils;
import com.prezi.spaghetti.obfuscation.ModuleObfuscator;
import com.prezi.spaghetti.obfuscation.ObfuscationParameters;
import com.prezi.spaghetti.obfuscation.ObfuscationResult;
import com.prezi.spaghetti.structure.OutputType;
import io.airlift.command.Command;
import io.airlift.command.Option;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
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
			description = "Output directory of ZIP file",
			required = true)
	private File output;

	@Option(name = {"-V", "--version"},
			description = "Version of the module")
	private String version;

	@Option(name = {"-r", "--resources"},
			description = "Resources directory")
	private File resourcesDirectory;

	//
	// Obfuscation parameters
	//

	@Option(name = {"--obfuscate"},
			description = "Obfuscate the output with Closure compiler")
	private boolean obfuscate;

	@Option(name = {"--symbols"},
			description = "Comma delimited list of additional symbols to protect during obfuscation")
	private String additionalSymbols;

	@Option(name = {"--externs"},
			description = "List of Closure extern files to use during obfuscation, delimited by colon (':')")
	private String externsPath;

	@Option(name = {"--work-dir"},
			description = "Obfuscation work directory")
	private File workDir;

	@Override
	public Integer call() throws Exception {
		OutputType type = OutputType.fromString(this.type, output);

		ModuleConfiguration config = parseConfig();
		ModuleNode moduleNode = config.getLocalModule();

		String sourceMap;
		if (sourceMapFile != null) {
			sourceMap = Files.asCharSource(sourceMapFile, Charsets.UTF_8).read();
		} else {
			sourceMap = null;
		}

		String javaScript = Files.asCharSource(sourceFile, Charsets.UTF_8).read();
		JavaScriptBundleProcessor javaScriptBundleProcessor = Generators.getService(JavaScriptBundleProcessor.class, language);
		JavaScriptBundleProcessorParameters processorParams = new DefaultJavaScriptBundleProcessorParameters(config);
		String processedJavaScript = InternalGeneratorUtils.bundleJavaScript(javaScriptBundleProcessor.processModuleJavaScript(processorParams, javaScript));

		SortedSet<String> dependentModules = Sets.newTreeSet();
		for (ModuleNode dependentModule : config.getDirectDependentModules()) {
			dependentModules.add(dependentModule.getName());
		}

		if (obfuscate) {
			ObfuscationResult result = obfuscate(config, processedJavaScript, sourceMap);
			processedJavaScript = result.javaScript;
			sourceMap = result.sourceMap;
		}

		ModuleBundleParameters params = new ModuleBundleParameters(
				moduleNode.getName(),
				moduleNode.getSource().getContents(),
				!Strings.isNullOrEmpty(version) ? version : "unspecified",
				sourceBaseUrl,
				processedJavaScript,
				sourceMap,
				dependentModules,
				resourcesDirectory);

		ModuleBundleFactory.create(type, output, params);
		return 0;
	}

	private ObfuscationResult obfuscate(ModuleConfiguration config, String javaScript, String sourceMap) throws IOException {
		if (workDir == null) {
			workDir = Files.createTempDir();
		}

		Set<File> externs = Sets.newLinkedHashSet();
		if (!Strings.isNullOrEmpty(externsPath)) {
			for (String externPath : externsPath.split(":")) {
				File externFile = new File(externPath);
				if (!externFile.exists()) {
					throw new SpaghettiCliException("Cannot find extern file: " + externPath);
				}
				externs.add(externFile);
			}
		}

		Set<String> additionalSymbolsSet;
		if (!Strings.isNullOrEmpty(additionalSymbols)) {
			additionalSymbolsSet = Sets.newLinkedHashSet(Arrays.asList(additionalSymbols.split(",")));
		} else {
			additionalSymbolsSet = Collections.emptySet();
		}

		JavaScriptBundleProcessor processor = Generators.getService(JavaScriptBundleProcessor.class, language);
		ModuleObfuscator obfuscator = new ModuleObfuscator(processor.getProtectedSymbols());
		return obfuscator.obfuscateModule(new ObfuscationParameters(
				config,
				config.getLocalModule(),
				javaScript,
				sourceMap,
				null,
				System.getenv("NODE_PATH"),
				externs,
				additionalSymbolsSet,
				workDir
		));
	}
}
