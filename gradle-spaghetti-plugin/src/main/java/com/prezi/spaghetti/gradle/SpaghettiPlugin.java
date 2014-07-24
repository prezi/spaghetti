package com.prezi.spaghetti.gradle;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.GeneratorFactory;
import com.prezi.spaghetti.Platforms;
import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.language.base.FunctionalSourceSet;
import org.gradle.language.base.LanguageSourceSet;
import org.gradle.language.base.ProjectSourceSet;
import org.gradle.language.base.plugins.LanguageBasePlugin;
import org.gradle.runtime.base.BinaryContainer;
import org.gradle.runtime.base.internal.BinaryNamingScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.Collection;
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

	public static <T> void registerSpaghettiModuleBinary(Project project, String moduleName, Callable<File> javaScriptFile, Callable<File> sourceMapFile, Collection<?> dependencies, T payload, SpaghettiModuleFactory<T> callback) {
		BinaryContainer binaryContainer = project.getExtensions().getByType(BinaryContainer.class);
		SpaghettiExtension spaghettiExtension = project.getExtensions().getByType(SpaghettiExtension.class);
		BinaryNamingScheme namingScheme = new SpaghettiModuleNamingScheme(moduleName);

		// Bundle module
		BundleModule bundleTask = createBundleTask(project, namingScheme, javaScriptFile, sourceMapFile, dependencies);
		Zip zipModule = createZipTask(project, namingScheme, bundleTask, namingScheme.getLifecycleTaskName(), "");
		logger.debug("Added bundle task {} with zip task {}", bundleTask, zipModule);

		// Obfuscate bundle
		ObfuscateModule obfuscateTask = createObfuscateTask(project, namingScheme, javaScriptFile, sourceMapFile, dependencies);
		Zip zipObfuscated = createZipTask(project, namingScheme, obfuscateTask, namingScheme.getLifecycleTaskName() + "-obfuscated", "obfuscated");
		logger.debug("Added obfuscate task {} with zip artifact {}", obfuscateTask, zipObfuscated);

		SpaghettiModuleData data = new DefaultSpaghettiModuleData(javaScriptFile, sourceMapFile, bundleTask, obfuscateTask, zipModule, zipObfuscated);
		SpaghettiModule moduleBinary = callback.create(namingScheme, data, payload);
		if (dependencies != null && !dependencies.isEmpty()) {
			moduleBinary.builtBy(dependencies);
		}

		if (!moduleBinary.isUsedForTesting()) {
			project.getArtifacts().add(spaghettiExtension.getConfiguration().getName(), zipModule);
			project.getArtifacts().add(spaghettiExtension.getObfuscatedConfiguration().getName(), zipObfuscated, new Closure(project) {
				@Override
				public Object call(Object... arguments) {
					ArchivePublishArtifact artifact = (ArchivePublishArtifact) this.getDelegate();
					artifact.setClassifier("obfuscated");
					return artifact;
				}
			});
		}

		binaryContainer.add(moduleBinary);
	}

	private static BundleModule createBundleTask(final Project project, final BinaryNamingScheme namingScheme, Callable<File> javaScriptFile, Callable<File> sourceMapFile, Collection<?> dependencies) {
		String bundleTaskName = namingScheme.getTaskName("bundle");
		BundleModule bundleTask = project.getTasks().create(bundleTaskName, BundleModule.class);
		bundleTask.setDescription("Bundles " + namingScheme.getDescription() + " module.");
		configureBundleTask(project, bundleTask, namingScheme, javaScriptFile, sourceMapFile, dependencies, "bundled");
		return bundleTask;
	}

	private static ObfuscateModule createObfuscateTask(final Project project, final BinaryNamingScheme namingScheme, Callable<File> javaScriptFile, Callable<File> sourceMapFile, Collection<?> dependencies) {
		String obfuscateTaskName = namingScheme.getTaskName("obfuscate");
		ObfuscateModule obfuscateTask = project.getTasks().create(obfuscateTaskName, ObfuscateModule.class);
		obfuscateTask.setDescription("Obfuscates " + namingScheme.getDescription() + " module.");
		configureBundleTask(project, obfuscateTask, namingScheme, javaScriptFile, sourceMapFile, dependencies, "obfuscated");
		return obfuscateTask;
	}

	private static void configureBundleTask(final Project project, AbstractBundleModuleTask task, final BinaryNamingScheme namingScheme, Callable<File> javaScriptFile, Callable<File> sourceMapFile, Collection<?> dependencies, final String outputDir) {
		task.getConventionMapping().map("inputFile", javaScriptFile);
		if (sourceMapFile != null) {
			task.getConventionMapping().map("sourceMap", sourceMapFile);
		}
		task.getConventionMapping().map("outputDirectory", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return project.file(project.getBuildDir() + "/spaghetti/" + namingScheme.getOutputDirectoryBase() + "/" + outputDir);
			}
		});
		if (dependencies != null && !dependencies.isEmpty()) {
			task.dependsOn(dependencies);
		}
	}

	private static Zip createZipTask(Project project, BinaryNamingScheme namingScheme, final AbstractBundleModuleTask bundleTask, final String name, String taskName) {
		String zipTaskName = namingScheme.getTaskName("zip", taskName);
		Zip zipTask = project.getTasks().create(zipTaskName, Zip.class);
		zipTask.setDescription("Zip up " + name + " " + namingScheme.getDescription() + ".");
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
