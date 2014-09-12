package com.prezi.spaghetti.cli.commands;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.prezi.spaghetti.Generator;
import com.prezi.spaghetti.Platforms;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleFactory;
import com.prezi.spaghetti.cli.SpaghettiCliException;
import com.prezi.spaghetti.config.ModuleConfiguration;
import com.prezi.spaghetti.config.ModuleConfigurationParser;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import io.airlift.command.Command;
import io.airlift.command.Option;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Command(name = "generate-headers", description = "Generates headers in the implementation language")
public class GenerateHeadersCommand extends AbstractCommand {
	@Option(name = {"-l", "--language"},
			description = "Implementation language",
			required = true)
	private String language;

	@Option(name = {"-m", "--definition"},
			description = "The module definition",
			required = true)
	private File definition;

	@Option(name = {"-d", "--direct-dependency-path", "--dependency-path"},
			description = "List of directly dependent module bundles separated by colon (':')")
	private String directDependencyPath;

	@Option(name = {"-t", "--transitive-dependency-path"},
			description = "List of transitively dependent module bundles separated by colon (':')")
	private String transitiveDependencyPath;

	@Option(name = {"-o", "--output", "--output-directory"},
			description = "Output directory",
			required = true)
	private File outputDirectory;

	@Option(name = {"-f", "--force"},
			description = "Overwrite output directory if exists")
	private boolean force;

	@Override
	public Integer call() throws Exception {
		if (!force && outputDirectory.exists()) {
			throw new SpaghettiCliException("Output directory exists: " + outputDirectory);
		}

		Collection<ModuleDefinitionSource> localSources  = Collections.singleton(parseSource(definition));
		Collection<ModuleDefinitionSource> dependentSources = parseSourceBundles(directDependencyPath);
		Collection<ModuleDefinitionSource> transitiveSources = parseSourceBundles(transitiveDependencyPath);
		ModuleConfiguration config = ModuleConfigurationParser.parse(localSources, dependentSources, transitiveSources);
		Generator generator = Platforms.createGeneratorForPlatform(language, config);
		FileUtils.deleteDirectory(outputDirectory);
		FileUtils.forceMkdir(outputDirectory);
		generator.generateHeaders(outputDirectory);
		return 0;
	}

	private static Collection<ModuleDefinitionSource> parseSourceBundles(String path) throws IOException {
		Collection<ModuleDefinitionSource> sources = Sets.newLinkedHashSet();
		if (!Strings.isNullOrEmpty(path)) {
			for (String bundlePath : path.split(":")) {
				File bundleFile = new File(bundlePath);
				ModuleBundle bundle = ModuleBundleFactory.load(bundleFile);
				sources.add(new ModuleDefinitionSource(bundleFile.getPath(), bundle.getDefinition()));
			}
		}
		return sources;
	}

	private static ModuleDefinitionSource parseSource(File file) throws IOException {
		String contents = Files.asCharSource(file, Charsets.UTF_8).read();
		return new ModuleDefinitionSource(file.getPath(), contents);
	}
}
