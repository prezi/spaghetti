package com.prezi.spaghetti.haxe.gradle;

import com.google.common.util.concurrent.Callables;
import com.prezi.haxe.gradle.DefaultHaxeSourceSet;
import com.prezi.haxe.gradle.Har;
import com.prezi.haxe.gradle.HaxeBasePlugin;
import com.prezi.haxe.gradle.HaxeBinary;
import com.prezi.haxe.gradle.HaxeBinaryBase;
import com.prezi.haxe.gradle.HaxeCompile;
import com.prezi.haxe.gradle.HaxeExtension;
import com.prezi.haxe.gradle.HaxeTestBinary;
import com.prezi.haxe.gradle.incubating.FunctionalSourceSet;
import com.prezi.spaghetti.bundle.ModuleBundleFactory;
import com.prezi.spaghetti.gradle.PackageApplication;
import com.prezi.spaghetti.gradle.SpaghettiBasePlugin;
import com.prezi.spaghetti.gradle.SpaghettiPlugin;
import com.prezi.spaghetti.gradle.internal.SpaghettiExtension;
import com.prezi.spaghetti.gradle.internal.SpaghettiGeneratedSourceSet;
import com.prezi.spaghetti.gradle.internal.SpaghettiModule;
import com.prezi.spaghetti.gradle.internal.SpaghettiModuleData;
import com.prezi.spaghetti.gradle.internal.SpaghettiModuleFactory;
import com.prezi.spaghetti.gradle.internal.incubating.BinaryNamingScheme;
import com.prezi.spaghetti.haxe.gradle.internal.HaxeSpaghettiModule;
import com.prezi.spaghetti.packaging.ApplicationType;
import org.apache.tools.ant.taskdefs.Execute;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Exec;
import org.gradle.internal.reflect.Instantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.prezi.haxe.gradle.nodetest.HaxeNodeTestCompile;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Add Spaghetti support to Haxe.
 */
public class SpaghettiHaxePlugin implements Plugin<Project> {
	private static final Logger logger = LoggerFactory.getLogger(SpaghettiHaxePlugin.class);

	private final Instantiator instantiator;
	private final FileResolver fileResolver;

	@Inject
	public SpaghettiHaxePlugin(Instantiator instantiator, FileResolver fileResolver) {
		this.instantiator = instantiator;
		this.fileResolver = fileResolver;
	}

	@Override
	public void apply(final Project project) {
		// Spaghetti will be working with Haxe, might as well set it
		project.getPlugins().apply(SpaghettiBasePlugin.class);
		SpaghettiExtension spaghettiExtension = project.getExtensions().getByType(SpaghettiExtension.class);
		spaghettiExtension.setLanguage("haxe");

		project.getPlugins().apply(HaxeBasePlugin.class);
		project.getPlugins().apply(SpaghettiPlugin.class);

		final HaxeExtension haxeExtension = project.getExtensions().getByType(HaxeExtension.class);

		// We'll be needing a "js" platform
		haxeExtension.getTargetPlatforms().maybeCreate("js");

		// Add Spaghetti generated sources to compile and source tasks
		spaghettiExtension.getSources().getByName("main").withType(SpaghettiGeneratedSourceSet.class).all(new Action<SpaghettiGeneratedSourceSet>() {
			@Override
			public void execute(final SpaghettiGeneratedSourceSet spaghettiGeneratedSourceSet) {
				addSpaghettiSourceSet(project, haxeExtension, spaghettiGeneratedSourceSet, HaxeBinary.class, "spaghetti");
			}
		});

		// Add Spaghetti generated test sources to test compile and test source tasks
		spaghettiExtension.getSources().getByName("test").withType(SpaghettiGeneratedSourceSet.class).all(new Action<SpaghettiGeneratedSourceSet>() {
			@Override
			public void execute(final SpaghettiGeneratedSourceSet spaghettiGeneratedSourceSet) {
				addSpaghettiSourceSet(project, haxeExtension, spaghettiGeneratedSourceSet, HaxeTestBinary.class, "spaghetti");
			}
		});

		// For every Haxe binary...
		haxeExtension.getBinaries().withType(HaxeBinary.class).all(new Action<HaxeBinary>() {
			@Override
			public void execute(final HaxeBinary binary) {
				// Add a compile, source and munit task
				HaxeBasePlugin.createCompileTask(project, binary, HaxeCompile.class);
				HaxeBasePlugin.createSourceTask(project, binary, Har.class);

				registerSpaghettiModuleBinary(project, binary, Collections.singleton(binary), false);
			}
		});

		haxeExtension.getBinaries().withType(HaxeTestBinary.class).all(new Action<HaxeTestBinary>() {
			@Override
			public void execute(final HaxeTestBinary testBinary) {
				HaxeBasePlugin.createTestCompileTask(project, testBinary, testBinary.getCompileClass());

				registerSpaghettiModuleBinary(project, testBinary, Collections.singleton(testBinary.getCompileTask()), true);
			}
		});
		final File nodeModulesDir =new File(project.getBuildDir(), "munit/node_modules");
		final Task npmTask = createSetupNodeDependenciesTask(project, nodeModulesDir);
		spaghettiExtension.getBinaries().withType(HaxeSpaghettiModule.class).all(new Action<HaxeSpaghettiModule>() {
			@Override
			public void execute(final HaxeSpaghettiModule moduleBinary) {
				HaxeBinaryBase<?> binary = moduleBinary.getOriginal();
				if (moduleBinary.isUsedForTesting() && binary instanceof HaxeTestBinary) {
					HaxeTestBinary testBinary = (HaxeTestBinary) binary;
					final PackageApplication appTask;

					MUnitWithSpaghetti munitTask = null;
					if (((HaxeTestBinary) binary).getCompileTask() instanceof HaxeNodeTestCompile) {
						NodeTestWithSpaghetti nodeTestWithSpaghetti = HaxeBasePlugin.createMUnitTask(project, testBinary, NodeTestWithSpaghetti.class);
						nodeTestWithSpaghetti.getConventionMapping().map("nodeModulesDirectory", new Callable<File>() {
							@Override
							public File call() throws Exception {
								return nodeModulesDir;
							}
						});
						munitTask = nodeTestWithSpaghetti;
						munitTask.dependsOn(npmTask);
						appTask = createTestApplication(moduleBinary, testBinary, ApplicationType.COMMON_JS);
					} else {
						munitTask = HaxeBasePlugin.createMUnitTask(project, testBinary, MUnitWithSpaghetti.class);
						appTask = createTestApplication(moduleBinary, testBinary, ApplicationType.AMD);
					}
					munitTask.getConventionMapping().map("testApplication", new Callable<File>() {
						@Override
						public File call() throws Exception {
							return appTask.getOutputDirectory();
						}
					});
					munitTask.getConventionMapping().map("testApplicationName", new Callable<String>() {
						@Override
						public String call() throws Exception {
							return appTask.getApplicationName();
						}

					});
					munitTask.dependsOn(appTask);
				}
			}

			private PackageApplication createTestApplication(final HaxeSpaghettiModule moduleBinary, final HaxeTestBinary testBinary, ApplicationType applicationType) {
				String packageTaskName = testBinary.getNamingScheme().getTaskName("package");

				PackageApplication appBundleTask = project.getTasks().create(packageTaskName, PackageApplication.class);
				appBundleTask.setDescription("Creates a testable application of " + testBinary);
				appBundleTask.setGroup("test");
				appBundleTask.getConventionMapping().map("outputDirectory", new Callable<File>() {
					@Override
					public File call() throws Exception {
						return project.file(project.getBuildDir() + "/spaghetti/tests/" + testBinary.getName());
					}
				});
				appBundleTask.getConventionMapping().map("dependentModules", new Callable<FileCollection>() {
					@Override
					public FileCollection call() throws Exception {
						return project.files(moduleBinary.getBundleTask().getDependentModules(), moduleBinary.getBundleTask().getOutputDirectory());
					}
				});
				appBundleTask.getConventionMapping().map("mainModule", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return ModuleBundleFactory.load(moduleBinary.getBundleTask().getOutputDirectory()).getName();
					}
				});
				appBundleTask.getConventionMapping().map("applicationName", Callables.returning(testBinary.getName() + "_test.js"));
				appBundleTask.getConventionMapping().map("type", Callables.returning(applicationType));
				appBundleTask.getConventionMapping().map("execute", Callables.returning(false));
				appBundleTask.dependsOn(moduleBinary.getBundleTask());
				return appBundleTask;
			}
		});
	}

	private Task createSetupNodeDependenciesTask(final Project project, File nodeModulesDir) {
		final Configuration npmTestConfig = project.getConfigurations().maybeCreate("npmMunitTest");
		List<String> dependencies = Arrays.asList("npm:requirejs:2.1.8",
				"npm:jquery:2.1.1",
				"npm:jsdom:0.10.6-3",
				"npm:cssstyle:0.2.14",
				"npm:assert:1.1.1",
				"npm:chai:1.9.0",
				"npm:sinon:1.9.1",
				"npm:mocha:1.17.1",
				"npm:istanbul:0.2.16",
				"npm:mocha-istanbul:0.2.0",
				"npm:spec-xunit-file:0.0.1-2",
				"npm:canvas:1.2.1");

		DependencyHandler dependencyHandler = project.getDependencies();
		for (String dependency : dependencies) {
			dependencyHandler.add(npmTestConfig.getName(), dependencyHandler.create(dependency));
		}

		Copy copyModulesTask = project.getTasks().create("copyNpmMunitTestDependencies", Copy.class);
		copyModulesTask.from(new Callable<ConfigurableFileCollection>() {
			@Override
			public ConfigurableFileCollection call() throws Exception {
				ConfigurableFileCollection unzippedFiles = project.files();
				for (File file : npmTestConfig) {
					unzippedFiles.from(project.zipTree(file));
				}
				return unzippedFiles;
			}
		});
		copyModulesTask.into(Callables.returning(new File(project.getBuildDir(), "munit/node_modules")));
		Exec makeNodeCanvasTask = project.getTasks().create("makeNodeCanvasMunitTestDependencies", Exec.class);
		makeNodeCanvasTask.setWorkingDir(new File(nodeModulesDir, "canvas"));
		makeNodeCanvasTask.setCommandLine("make");
		makeNodeCanvasTask.dependsOn(copyModulesTask);
		return makeNodeCanvasTask;
	}

	private <T extends HaxeBinaryBase<?>> void addSpaghettiSourceSet(final Project project, HaxeExtension haxeExtension, final SpaghettiGeneratedSourceSet spaghettiSourceSet, Class<T> binaryType, String sourceSetName) {
		logger.debug("Adding {} to binaries in {}", spaghettiSourceSet, project.getPath());
		Configuration config = project.getConfigurations().maybeCreate("spaghetti");
		FunctionalSourceSet spaghetti = haxeExtension.getSources().maybeCreate(sourceSetName);
		final DefaultHaxeSourceSet haxeSourceSet = instantiator.newInstance(DefaultHaxeSourceSet.class, spaghettiSourceSet.getName(), spaghetti, config, fileResolver);
		haxeSourceSet.getSource().source(spaghettiSourceSet.getSource());
		haxeSourceSet.builtBy(spaghettiSourceSet);
		haxeExtension.getBinaries().withType(binaryType).all(new Action<T>() {
			@Override
			public void execute(T compiledBinary) {
				compiledBinary.getSource().add(haxeSourceSet);
				logger.debug("Added {} to {} in {}", spaghettiSourceSet, compiledBinary, project.getPath());
			}
		});
	}

	private void registerSpaghettiModuleBinary(Project project, final HaxeBinaryBase<?> binary, Collection<?> dependencies, final boolean testing) {
		Callable<File> javaScriptFile = new Callable<File>() {
			@Override
			public File call() throws Exception {
				return binary.getCompileTask().getOutputFile();
			}
		};
		SpaghettiPlugin.registerSpaghettiModuleBinary(project, binary.getName(), javaScriptFile, null, dependencies, binary, new SpaghettiModuleFactory<HaxeBinaryBase<?>>() {
			@Override
			public SpaghettiModule create(BinaryNamingScheme namingScheme, SpaghettiModuleData data, HaxeBinaryBase<?> original) {
				return new HaxeSpaghettiModule(namingScheme, data, original, testing);
			}
		});
	}
}
