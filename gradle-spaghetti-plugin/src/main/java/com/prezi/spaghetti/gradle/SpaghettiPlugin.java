package com.prezi.spaghetti.gradle;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.GeneratorFactory;
import com.prezi.spaghetti.Platforms;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.language.base.BinaryContainer;
import org.gradle.language.base.FunctionalSourceSet;
import org.gradle.language.base.LanguageSourceSet;
import org.gradle.language.base.ProjectSourceSet;
import org.gradle.language.base.internal.BinaryInternal;
import org.gradle.language.base.internal.BinaryNamingScheme;
import org.gradle.language.base.plugins.LanguageBasePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class SpaghettiPlugin implements Plugin<Project> {
	private static final Logger logger = LoggerFactory.getLogger(SpaghettiPlugin.class);
	private static final Pattern MODULE_FILE_PATTERN = Pattern.compile(".+\\.module");
	private final Instantiator instantiator;
	private final FileResolver fileResolver;

	@Inject
	public SpaghettiPlugin(Instantiator instantiator, FileResolver fileResolver) {
		this.instantiator = instantiator;
		this.fileResolver = fileResolver;
	}

	@Override
	public void apply(final Project project) {
		project.getPlugins().apply(LanguageBasePlugin.class);
		project.getPlugins().apply(SpaghettiBasePlugin.class);

		createPlatformsTask(project);

		BinaryContainer binaryContainer = project.getExtensions().getByType(BinaryContainer.class);
		ProjectSourceSet projectSourceSet = project.getExtensions().getByType(ProjectSourceSet.class);
		final SpaghettiExtension extension = project.getExtensions().getByType(SpaghettiExtension.class);

		// Add source sets
		FunctionalSourceSet functionalSourceSet = projectSourceSet.maybeCreate("main");

		DefaultSpaghettiSourceSet spaghettiSourceSet = instantiator.newInstance(DefaultSpaghettiSourceSet.class, "spaghetti", functionalSourceSet, fileResolver);
		spaghettiSourceSet.getSource().srcDir("src/main/spaghetti");
		functionalSourceSet.add(spaghettiSourceSet);

		DefaultSpaghettiResourceSet spaghettiResourceSet = instantiator.newInstance(DefaultSpaghettiResourceSet.class, "spaghetti-resources", functionalSourceSet, fileResolver);
		spaghettiResourceSet.getSource().srcDir("src/main/spaghetti-resources");
		functionalSourceSet.add(spaghettiResourceSet);

		// TODO Use a proper Spaghetti module binary to tie this together
		final ProcessSpaghettiResources resourcesTask = project.getTasks().create("processSpaghettiResources", ProcessSpaghettiResources.class);
		resourcesTask.setDescription("Processes Spaghetti resources");
		resourcesTask.getConventionMapping().map("destinationDir", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return project.file(String.valueOf(project.getBuildDir()) + "/spaghetti/resources");
			}

		});
		resourcesTask.dependsOn(spaghettiResourceSet);
		resourcesTask.from(spaghettiResourceSet.getSource());

		project.getTasks().withType(AbstractDefinitionAwareSpaghettiTask.class).all(new Action<AbstractDefinitionAwareSpaghettiTask>() {
			@Override
			public void execute(AbstractDefinitionAwareSpaghettiTask task) {
				task.getConventionMapping().map("definitions", new Callable<FileCollection>() {
					@Override
					public FileCollection call() throws Exception {
						return findDefinitions(project);
					}

				});
			}

		});
		project.getTasks().withType(AbstractBundleModuleTask.class).all(new Action<AbstractBundleModuleTask>() {
			@Override
			public void execute(AbstractBundleModuleTask task) {
				task.getConventionMapping().map("sourceBaseUrl", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return extension.getSourceBaseUrl();
					}

				});
				task.getConventionMapping().map("resourcesDirectoryInternal", new Callable<File>() {
					@Override
					public File call() throws Exception {
						return resourcesTask.getDestinationDir();
					}

				});
				task.dependsOn(resourcesTask);
			}

		});

		// Automatically generate module headers
		final GenerateHeaders generateTask = project.getTasks().create("generateHeaders", GenerateHeaders.class);
		generateTask.setDescription("Generates Spaghetti headers.");
		logger.debug("Created {}", generateTask);

		// Create source set
		LanguageSourceSet spaghettiGeneratedSourceSet = functionalSourceSet.findByName("spaghetti-generated");
		if (spaghettiGeneratedSourceSet == null) {
			spaghettiGeneratedSourceSet = instantiator.newInstance(DefaultSpaghettiGeneratedSourceSet.class, "spaghetti-generated", functionalSourceSet, fileResolver);
			functionalSourceSet.add(spaghettiGeneratedSourceSet);
			logger.debug("Added {}", spaghettiGeneratedSourceSet);
		}

		spaghettiGeneratedSourceSet.getSource().srcDir(new Callable<File>() {
			@Override
			public File call() throws Exception {
				return generateTask.getOutputDirectory();
			}

		});
		spaghettiGeneratedSourceSet.builtBy(generateTask);

		binaryContainer.withType(SpaghettiCompatibleJavaScriptBinary.class).all(new Action<SpaghettiCompatibleJavaScriptBinary>() {
			@Override
			public void execute(final SpaghettiCompatibleJavaScriptBinary binary) {
				logger.debug("Creating bundle and obfuscation for " + String.valueOf(binary));

				// TODO Use a proper Spaghetti module binary instead of passing the resourcesTask around
				// Automatically create bundle module task and artifact
				BundleModule bundleTask = createBundleTask(project, binary);
				Zip zipModule = createZipTask(project, binary, bundleTask, binary.getName(), "");
				logger.debug("Added bundle task {} with zip task {}", bundleTask, zipModule);
				if (!binary.isUsedForTesting()) {
					project.getArtifacts().add(extension.getConfiguration().getName(), zipModule);
					logger.debug("Added bundle artifact for {}", binary);
				}


				// Automatically obfuscate bundle
				ObfuscateModule obfuscateTask = createObfuscateTask(project, binary);
				Zip zipObfuscated = createZipTask(project, binary, obfuscateTask, binary.getName() + "-obfuscated", "obfuscated");
				logger.debug("Added obfuscate task {} with zip artifact {}", obfuscateTask, zipObfuscated);
				if (!binary.isUsedForTesting()) {
					project.getArtifacts().add(extension.getObfuscatedConfiguration().getName(), zipObfuscated);
					logger.debug("Added obfuscated bundle artifact for {}", binary);
				}

			}

		});
	}

	private static void createPlatformsTask(Project project) {
		if (project.getTasks().findByName("spaghetti-platforms") != null) {
			return;

		}

		Task platformsTask = project.getTasks().create("spaghetti-platforms");
		platformsTask.setGroup("help");
		platformsTask.setDescription("Show supported Spaghetti platforms.");
		platformsTask.doLast(new Action<Task>() {
			@Override
			public void execute(Task task) {
				Set<GeneratorFactory> factories = Platforms.getGeneratorFactories();
				if (factories.isEmpty()) {
					System.out.println("No platform support for Spaghetti is found");
				} else {
					System.out.println("Spaghetti supports the following platforms:\n");
					int length = 0;
					for (GeneratorFactory factory : factories) {
						length = Math.max(length, factory.getPlatform().length());
					}

					for (GeneratorFactory factory : factories) {
						System.out.println("  " + Strings.padEnd(factory.getPlatform(), length, ' ') + " - " + factory.getDescription());
					}

				}

			}

		});
	}

	private static BundleModule createBundleTask(final Project project, final SpaghettiCompatibleJavaScriptBinary binary) {
		BinaryNamingScheme namingScheme = ((BinaryInternal) binary).getNamingScheme();
		String bundleTaskName = namingScheme.getTaskName("bundle");
		BundleModule bundleTask = project.getTasks().create(bundleTaskName, BundleModule.class);
		bundleTask.setDescription("Bundles " + String.valueOf(binary) + " module.");
		bundleTask.getConventionMapping().map("inputFile", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return binary.getJavaScriptFile();
			}

		});
		bundleTask.getConventionMapping().map("sourceMap", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return binary.getSourceMapFile();
			}

		});
		bundleTask.getConventionMapping().map("outputDirectory", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return new File(project.getBuildDir(), "spaghetti/bundle/" + binary.getName());
			}

		});
		bundleTask.dependsOn(binary);
		binary.setBundleTask(bundleTask);
		return bundleTask;
	}

	private static ObfuscateModule createObfuscateTask(final Project project, final SpaghettiCompatibleJavaScriptBinary binary) {
		BinaryNamingScheme namingScheme = ((BinaryInternal) binary).getNamingScheme();
		String obfuscateTaskName = namingScheme.getTaskName("obfuscate");
		ObfuscateModule obfuscateTask = project.getTasks().create(obfuscateTaskName, ObfuscateModule.class);
		obfuscateTask.setDescription("Obfuscates " + String.valueOf(binary) + " module.");
		obfuscateTask.getConventionMapping().map("inputFile", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return binary.getJavaScriptFile();
			}

		});
		obfuscateTask.getConventionMapping().map("sourceMap", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return binary.getSourceMapFile();
			}

		});
		obfuscateTask.getConventionMapping().map("outputDirectory", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return new File(project.getBuildDir(), "spaghetti/obfuscation/" + binary.getName());
			}

		});
		obfuscateTask.dependsOn(binary);
		binary.setObfuscateTask(obfuscateTask);
		return obfuscateTask;
	}

	private static Zip createZipTask(Project project, final SpaghettiCompatibleJavaScriptBinary binary, final AbstractBundleModuleTask bundleTask, final String name, String taskName) {
		BinaryNamingScheme namingScheme = ((BinaryInternal) binary).getNamingScheme();
		String zipTaskName = namingScheme.getTaskName("zip", taskName);
		Zip zipTask = project.getTasks().create(zipTaskName, Zip.class);
		zipTask.setDescription("Zip up " + name + " " + String.valueOf(binary) + ".");
		zipTask.dependsOn(bundleTask);
		zipTask.from(new Callable<File>() {
			@Override
			public File call() throws Exception {
				return bundleTask.getOutputDirectory();
			}

		});
		zipTask.getConventionMapping().map("baseName", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return name;
			}

		});
		return zipTask;
	}

	public static FileCollection findDefinitions(Project project) {
		Set<SpaghettiSourceSet> sources = project.getExtensions().getByType(ProjectSourceSet.class).getByName("main").withType(SpaghettiSourceSet.class);
		Set<File> definitions = Sets.newLinkedHashSet();
		for (SpaghettiSourceSet sourceSet : sources) {
			for (File sourceDir : sourceSet.getSource().getSrcDirs()) {
				if (sourceDir.isDirectory()) {
					File[] files = sourceDir.listFiles();
					if (files != null) {
						for (File file : files) {
							if (MODULE_FILE_PATTERN.matcher(file.getName()).matches()) {
								definitions.add(file);
							}
						}
					}
				}

			}
		}

		return project.files(definitions);
	}
}
