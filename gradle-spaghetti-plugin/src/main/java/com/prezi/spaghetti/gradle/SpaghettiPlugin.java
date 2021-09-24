package com.prezi.spaghetti.gradle;

import com.prezi.spaghetti.definition.DefinitionFile;
import com.prezi.spaghetti.gradle.internal.AbstractBundleModuleTask;
import com.prezi.spaghetti.gradle.internal.DefaultSpaghettiGeneratedSourceSet;
import com.prezi.spaghetti.gradle.internal.DefaultSpaghettiModuleData;
import com.prezi.spaghetti.gradle.internal.DefaultSpaghettiResourceSet;
import com.prezi.spaghetti.gradle.internal.DefaultSpaghettiSourceSet;
import com.prezi.spaghetti.gradle.internal.DefinitionAwareSpaghettiTask;
import com.prezi.spaghetti.gradle.internal.SpaghettiExtension;
import com.prezi.spaghetti.gradle.internal.SpaghettiModule;
import com.prezi.spaghetti.gradle.internal.SpaghettiModuleData;
import com.prezi.spaghetti.gradle.internal.SpaghettiModuleFactory;
import com.prezi.spaghetti.gradle.internal.SpaghettiModuleNamingScheme;
import com.prezi.spaghetti.gradle.internal.incubating.BinaryNamingScheme;
import com.prezi.spaghetti.gradle.internal.incubating.FunctionalSourceSet;
import com.prezi.spaghetti.gradle.internal.incubating.LanguageSourceSet;
import groovy.lang.Closure;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.internal.reflect.Instantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

public class SpaghettiPlugin implements Plugin<Project> {
	private static final Logger logger = LoggerFactory.getLogger(SpaghettiPlugin.class);

	public static final String SPAGHETTI_GENERATED_SOURCE_SET = "spaghetti-generated";

	private final Instantiator instantiator;
	//private final FileResolver fileResolver;
	private final ObjectFactory objectFactory;

	@Inject
	public SpaghettiPlugin(Instantiator instantiator, ObjectFactory objectFactory) {
		this.instantiator = instantiator;
		this.objectFactory = objectFactory;
	}

	@Override
	public void apply(final Project project) {
		project.getPlugins().apply(SpaghettiBasePlugin.class);

		final SpaghettiExtension extension = project.getExtensions().getByType(SpaghettiExtension.class);

		// Add source sets
		FunctionalSourceSet mainSources = extension.getSources().maybeCreate("main");
		FunctionalSourceSet testSources = extension.getSources().maybeCreate("test");

		DefaultSpaghettiSourceSet spaghettiSourceSet = instantiator.newInstance(DefaultSpaghettiSourceSet.class, "spaghetti", mainSources, objectFactory);
		spaghettiSourceSet.getSource().srcDir("src/main/spaghetti");
		mainSources.add(spaghettiSourceSet);

		DefaultSpaghettiResourceSet spaghettiResourceSet = instantiator.newInstance(DefaultSpaghettiResourceSet.class, "spaghetti-resources", mainSources, objectFactory);
		spaghettiResourceSet.getSource().srcDir("src/main/spaghetti-resources");
		mainSources.add(spaghettiResourceSet);

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

		project.getTasks().withType(DefinitionAwareSpaghettiTask.class).all(new Action<DefinitionAwareSpaghettiTask>() {
			private Callable<DefinitionFile> callable = new Callable<DefinitionFile>() {
				@Override
				public DefinitionFile call() throws Exception {
					return extension.getDefinition();
				}
			};

			@Override
			public void execute(DefinitionAwareSpaghettiTask task) {
				task.getConventionMapping().map("definition", callable);
			}
		});
		project.getTasks().withType(AbstractBundleModuleTask.class).all(new Action<AbstractBundleModuleTask>() {
			@Override
			public void execute(AbstractBundleModuleTask task) {
				task.dependsOn(spaghettiSourceSet.getBuildDependencies());
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
				task.getConventionMapping().map("lazyLoadable", new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return extension.isLazy();
					}

				});
				task.dependsOn(resourcesTask);
			}

		});

		// Automatically generate module headers
		GenerateHeaders generateHeaders = addGenerateHeadersTask(project, "generateHeaders", "generated-headers", mainSources);
		generateHeaders.setDescription("Generates Spaghetti headers.");

		// Automatically generate test headers
		GenerateHeaders generateTestHeaders = addGenerateHeadersTask(project, "generateTestHeaders", "generated-test-headers", testSources);
		generateTestHeaders.setDescription("Generates Spaghetti test headers.");
		SpaghettiBasePlugin.withDefaultTestConfiguration(project, generateTestHeaders);

		// Add task for generating stubs
		addGenerateStubsTask(project, testSources);
	}

	private GenerateHeaders addGenerateHeadersTask(final Project project, String name, final String directoryName, FunctionalSourceSet functionalSourceSet) {
		final GenerateHeaders generateHeadersTask = project.getTasks().create(name, GenerateHeaders.class);
		generateHeadersTask.setOutputDirectory(
			new File(project.getBuildDir(), "spaghetti/" + directoryName));
		logger.debug("Created {}", generateHeadersTask);

		// Create source set
		LanguageSourceSet spaghettiHeaders = functionalSourceSet.findByName(SPAGHETTI_GENERATED_SOURCE_SET);
		if (spaghettiHeaders == null) {
			spaghettiHeaders = instantiator.newInstance(DefaultSpaghettiGeneratedSourceSet.class, SPAGHETTI_GENERATED_SOURCE_SET, functionalSourceSet, objectFactory);
			functionalSourceSet.add(spaghettiHeaders);
			logger.debug("Added {}", spaghettiHeaders);
		}

		spaghettiHeaders.getSource().srcDir(new Callable<File>() {
			@Override
			public File call() throws Exception {
				return generateHeadersTask.getOutputDirectory();
			}
		});
		spaghettiHeaders.builtBy(generateHeadersTask);

		return generateHeadersTask;
	}

	private void addGenerateStubsTask(final Project project, FunctionalSourceSet functionalSourceSet) {
		final GenerateStubs generateStubsTask = project.getTasks().create("generateStubs", GenerateStubs.class);
		generateStubsTask.setDescription("Generates Spaghetti stubs.");
		logger.debug("Created {}", generateStubsTask);

		SpaghettiBasePlugin.withDefaultTestConfiguration(project, generateStubsTask);

		// Create source set
		LanguageSourceSet spaghettiStubs = functionalSourceSet.findByName(SPAGHETTI_GENERATED_SOURCE_SET);
		if (spaghettiStubs == null) {
			spaghettiStubs = instantiator.newInstance(DefaultSpaghettiGeneratedSourceSet.class, SPAGHETTI_GENERATED_SOURCE_SET, functionalSourceSet, objectFactory);
			functionalSourceSet.add(spaghettiStubs);
			logger.debug("Added {}", spaghettiStubs);
		}

		spaghettiStubs.getSource().srcDir(new Callable<File>() {
			@Override
			public File call() throws Exception {
				return generateStubsTask.getOutputDirectory();
			}
		});
		spaghettiStubs.builtBy(generateStubsTask);
	}

	public static <T> void registerSpaghettiModuleBinary(Project project, String moduleName, Callable<File> javaScriptFile, Callable<File> sourceMapFile, DefinitionOverride definitionOverride, Collection<?> dependencies, T payload, SpaghettiModuleFactory<T> callback) {
		SpaghettiExtension spaghettiExtension = project.getExtensions().getByType(SpaghettiExtension.class);
		BinaryNamingScheme namingScheme = new SpaghettiModuleNamingScheme(moduleName);


		// Bundle module
		BundleModule bundleTask = createBundleTask(project, namingScheme, javaScriptFile, sourceMapFile, definitionOverride, dependencies);
		Zip zipModule = createZipTask(project, namingScheme, bundleTask, namingScheme.getLifecycleTaskName(), "");
		logger.debug("Added bundle task {} with zip task {}", bundleTask, zipModule);

		// Bundle def
		BundleModule bundleDefinitionTask = createDefinitionBundleTask(project, namingScheme, definitionOverride, Collections.emptyList());
		Zip zipDefinition = createZipTask(project, namingScheme, bundleDefinitionTask, namingScheme.getLifecycleTaskName() + "-definition", "definition");
		logger.debug("Added bundle definition task {} with zip task {}", bundleDefinitionTask, zipDefinition);

		// Obfuscate bundle
		ObfuscateModule obfuscateTask = createObfuscateTask(project, namingScheme, javaScriptFile, sourceMapFile, definitionOverride, dependencies);
		Zip zipObfuscated = createZipTask(project, namingScheme, obfuscateTask, namingScheme.getLifecycleTaskName() + "-obfuscated", "obfuscated");
		logger.debug("Added obfuscate task {} with zip artifact {}", obfuscateTask, zipObfuscated);

		SpaghettiModuleData data = new DefaultSpaghettiModuleData(javaScriptFile, sourceMapFile, bundleTask, obfuscateTask, zipModule, zipObfuscated);
		SpaghettiModule moduleBinary = callback.create(namingScheme, data, payload);
		if (dependencies != null && !dependencies.isEmpty()) {
			moduleBinary.builtBy(dependencies);
		}

		if (!moduleBinary.isUsedForTesting()) {
			addBundleArtifact(project, spaghettiExtension.getConfiguration(), zipModule, "");
			addBundleArtifact(project, spaghettiExtension.getObfuscatedConfiguration(), zipObfuscated, "obfuscated");
			addBundleArtifact(project, spaghettiExtension.getModuleDefinitionConfiguration(), zipDefinition, "definition");
		} else {
			if (spaghettiExtension.getPublishTestArtifacts()) {
				addBundleArtifact(project, spaghettiExtension.getTestConfiguration(), zipModule, "test");
				addBundleArtifact(project, spaghettiExtension.getTestObfuscatedConfiguration(), zipObfuscated, "test-obfuscated");
			}
			SpaghettiBasePlugin.withDefaultTestConfiguration(project, bundleTask);
		}

		spaghettiExtension.getBinaries().add(moduleBinary);
	}

	private static void addBundleArtifact(Project project, Configuration configuration, Zip task, final String name) {
		project.getArtifacts().add(configuration.getName(), task, new Closure(project) {
			@Override
			public Object call(Object... arguments) {
				ArchivePublishArtifact artifact = (ArchivePublishArtifact) this.getDelegate();
				artifact.setClassifier(name);
				return artifact;
			}
		});
	}

	private static BundleModule createBundleTask(final Project project, final BinaryNamingScheme namingScheme, Callable<File> javaScriptFile, Callable<File> sourceMapFile, DefinitionOverride definitionOverride, Collection<?> dependencies) {
		String bundleTaskName = namingScheme.getTaskName("bundle");
		BundleModule bundleTask = project.getTasks().create(bundleTaskName, BundleModule.class);
		bundleTask.setDescription("Bundles " + namingScheme.getDescription() + " module.");
		configureBundleTask(project, bundleTask, namingScheme, javaScriptFile, sourceMapFile, definitionOverride, dependencies, "bundled");
		return bundleTask;
	}

	private static BundleModule createDefinitionBundleTask(final Project project, final BinaryNamingScheme namingScheme, DefinitionOverride definitionOverride, Collection<?> dependencies) {
		String bundleTaskName = namingScheme.getTaskName("bundleDef");
		BundleModule bundleTask = project.getTasks().create(bundleTaskName, BundleModule.class);
		if (definitionOverride != null && definitionOverride.getGeneratorTask() != null) {
			bundleTask.dependsOn(definitionOverride.getGeneratorTask());
		}
		bundleTask.setDescription("Bundles definition of " + namingScheme.getDescription() + " module.");
		configureBundleTask(project, bundleTask, namingScheme, null, null, definitionOverride, dependencies, "bundledDef");
		return bundleTask;
	}

	private static ObfuscateModule createObfuscateTask(final Project project, final BinaryNamingScheme namingScheme, Callable<File> javaScriptFile, Callable<File> sourceMapFile, DefinitionOverride definitionOverride, Collection<?> dependencies) {
		String obfuscateTaskName = namingScheme.getTaskName("obfuscate");
		ObfuscateModule obfuscateTask = project.getTasks().create(obfuscateTaskName, ObfuscateModule.class);
		obfuscateTask.setDescription("Obfuscates " + namingScheme.getDescription() + " module.");
		configureBundleTask(project, obfuscateTask, namingScheme, javaScriptFile, sourceMapFile, definitionOverride, dependencies, "obfuscated");
		return obfuscateTask;
	}

	private static void configureBundleTask(final Project project, AbstractBundleModuleTask task, final BinaryNamingScheme namingScheme, Callable<File> javaScriptFile, Callable<File> sourceMapFile, DefinitionOverride definitionOverride, Collection<?> dependencies, final String outputDir) {
		if (javaScriptFile != null) {
			task.getConventionMapping().map("inputFile", javaScriptFile);
		}
		if (sourceMapFile != null) {
			task.getConventionMapping().map("sourceMap", sourceMapFile);
		}
		if (definitionOverride != null && definitionOverride.getDefinitionFile() != null) {
			task.getConventionMapping().map("definitionOverride", definitionOverride.getDefinitionFile());
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
		zipTask.getArchiveBaseName().convention(name);
		return zipTask;
	}
}
