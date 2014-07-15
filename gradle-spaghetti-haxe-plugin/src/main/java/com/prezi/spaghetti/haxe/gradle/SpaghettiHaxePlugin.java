package com.prezi.spaghetti.haxe.gradle;

import com.prezi.haxe.gradle.Har;
import com.prezi.haxe.gradle.HaxeBasePlugin;
import com.prezi.haxe.gradle.HaxeBinary;
import com.prezi.haxe.gradle.HaxeBinaryBase;
import com.prezi.haxe.gradle.HaxeCompile;
import com.prezi.haxe.gradle.HaxeExtension;
import com.prezi.haxe.gradle.HaxeTestBinary;
import com.prezi.haxe.gradle.TargetPlatform;
import com.prezi.spaghetti.bundle.ModuleBundleFactory;
import com.prezi.spaghetti.gradle.PackageApplication;
import com.prezi.spaghetti.gradle.SpaghettiBasePlugin;
import com.prezi.spaghetti.gradle.SpaghettiExtension;
import com.prezi.spaghetti.gradle.SpaghettiGeneratedSourceSet;
import com.prezi.spaghetti.gradle.SpaghettiPlugin;
import com.prezi.spaghetti.packaging.ApplicationType;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.language.base.BinaryContainer;
import org.gradle.language.base.ProjectSourceSet;
import org.gradle.language.base.internal.BinaryInternal;
import org.gradle.language.base.internal.BinaryNamingScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.concurrent.Callable;

/**
 * Add Spaghetti support to Haxe.
 */
public class SpaghettiHaxePlugin implements Plugin<Project> {
	private static final Logger logger = LoggerFactory.getLogger(SpaghettiHaxePlugin.class);
	private final Instantiator instantiator;

	@Inject
	public SpaghettiHaxePlugin(Instantiator instantiator) {
		this.instantiator = instantiator;
	}

	@Override
	public void apply(final Project project) {
		// Spaghetti will be working with Haxe, might as well set it
		project.getPlugins().apply(SpaghettiBasePlugin.class);
		SpaghettiExtension spaghettiExtension = project.getExtensions().getByType(SpaghettiExtension.class);
		spaghettiExtension.setPlatform("haxe");

		project.getPlugins().apply(HaxeBasePlugin.class);
		project.getPlugins().apply(SpaghettiPlugin.class);

		final BinaryContainer binaryContainer = project.getExtensions().getByType(BinaryContainer.class);
		ProjectSourceSet projectSourceSet = project.getExtensions().getByType(ProjectSourceSet.class);
		HaxeExtension haxeExtension = project.getExtensions().getByType(HaxeExtension.class);

		// We'll be needing a "js" platform
		NamedDomainObjectContainer<TargetPlatform> targetPlatforms = haxeExtension.getTargetPlatforms();
		targetPlatforms.maybeCreate("js");

		// Tests should always depend on modules
		Configuration testConfiguration = project.getConfigurations().getByName("test");
		testConfiguration.extendsFrom(spaghettiExtension.getConfiguration());

		// Add Spaghetti generated sources to compile and source tasks
		projectSourceSet.findByName("main").withType(SpaghettiGeneratedSourceSet.class).all(new Action<SpaghettiGeneratedSourceSet>() {
			@Override
			public void execute(final SpaghettiGeneratedSourceSet spaghettiGeneratedSourceSet) {
				logger.debug("Adding {} to binaries in {}", spaghettiGeneratedSourceSet, project.getPath());
				binaryContainer.withType(HaxeBinaryBase.class).all(new Action<HaxeBinaryBase>() {
					@Override
					@SuppressWarnings("unchecked")
					public void execute(HaxeBinaryBase compiledBinary) {
						compiledBinary.getSource().add(spaghettiGeneratedSourceSet);
						logger.debug("Added {} to {} in {}", spaghettiGeneratedSourceSet, compiledBinary, project.getPath());
					}
				});
			}
		});

		// For every Haxe binary...
		binaryContainer.withType(HaxeBinary.class).all(new Action<HaxeBinary>() {
			@Override
			public void execute(HaxeBinary binary) {
				// Add a compile, source and munit task
				HaxeBasePlugin.createCompileTask(project, binary, HaxeCompile.class);
				HaxeBasePlugin.createSourceTask(project, binary, Har.class);

				// Create Spaghetti compatible binary
				DefaultHaxeCompiledSpaghettiCompatibleJavaScriptBinary jsBinary = instantiator.newInstance(DefaultHaxeCompiledSpaghettiCompatibleJavaScriptBinary.class, binary, false);
				jsBinary.builtBy(binary.getBuildDependencies());
				binaryContainer.add(jsBinary);
			}
		});

		binaryContainer.withType(HaxeTestBinary.class).all(new Action<HaxeTestBinary>() {
			@Override
			public void execute(final HaxeTestBinary testBinary) {
				HaxeBasePlugin.createTestCompileTask(project, testBinary, HaxeTestCompileWithSpaghetti.class);

				// Create Spaghetti compatible test binary
				final DefaultHaxeCompiledSpaghettiCompatibleJavaScriptBinary jsTestBinary = instantiator.newInstance(DefaultHaxeCompiledSpaghettiCompatibleJavaScriptBinary.class, testBinary, true);
				jsTestBinary.builtBy(new Callable<Task>() {
					@Override
					public Task call() throws Exception {
						return testBinary.getCompileTask();
					}
				});
				binaryContainer.add(jsTestBinary);

				final PackageApplication appTask = createTestApplication(jsTestBinary);

				MUnitWithSpaghetti munitTask = HaxeBasePlugin.createMUnitTask(project, testBinary, MUnitWithSpaghetti.class);
				munitTask.getConventionMapping().map("testApplication", new Callable<File>() {
					@Override
					public File call() throws Exception {
						return appTask.getOutputDirectory();
					}
				});
				munitTask.getConventionMapping().map("testApplicationName", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return jsTestBinary.getName() + "_test.js";
					}

				});
				munitTask.dependsOn(appTask);
			}

			private PackageApplication createTestApplication(final HaxeCompiledSpaghettiCompatibleJavaScriptBinary testBinary) {
				BinaryNamingScheme namingScheme = ((BinaryInternal) testBinary).getNamingScheme();
				String bundleTaskName = namingScheme.getTaskName("package");

				PackageApplication appBundleTask = project.getTasks().create(bundleTaskName, PackageApplication.class);
				appBundleTask.setDescription("Creates a testable application of " + testBinary);
				appBundleTask.setGroup("test");
				appBundleTask.getConventionMapping().map("outputDirectory", new Callable<File>() {
					@Override
					public File call() throws Exception {
						return project.file(project.getBuildDir() + "/spaghetti/tests/" + testBinary.getName());
					}
				});
				appBundleTask.getConventionMapping().map("additionalDirectDependentModulesInternal", new Callable<FileCollection>() {
					@Override
					public FileCollection call() throws Exception {
						return project.files(testBinary.getBundleTask().getOutputDirectory());
					}
				});
				appBundleTask.getConventionMapping().map("mainModule", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return ModuleBundleFactory.load(testBinary.getBundleTask().getOutputDirectory()).getName();
					}
				});
				appBundleTask.getConventionMapping().map("applicationName", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return testBinary.getName() + "_test.js";
					}
				});
				appBundleTask.getConventionMapping().map("type", new Callable<ApplicationType>() {
					@Override
					public ApplicationType call() throws Exception {
						return ApplicationType.AMD;
					}
				});
				appBundleTask.getConventionMapping().map("baseUrl", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return ".";
					}
				});
				appBundleTask.getConventionMapping().map("execute", new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return false;
					}
				});
				appBundleTask.dependsOn(testBinary.getBundleTask());
				return appBundleTask;
			}
		});
	}
}
