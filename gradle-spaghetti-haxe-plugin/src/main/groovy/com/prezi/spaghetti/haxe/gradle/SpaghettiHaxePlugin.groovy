package com.prezi.spaghetti.haxe.gradle

import com.prezi.haxe.gradle.Har
import com.prezi.haxe.gradle.HaxeBasePlugin
import com.prezi.haxe.gradle.HaxeBinary
import com.prezi.haxe.gradle.HaxeBinaryBase
import com.prezi.haxe.gradle.HaxeCompile
import com.prezi.haxe.gradle.HaxeExtension
import com.prezi.haxe.gradle.HaxeTestBinary
import com.prezi.spaghetti.bundle.ModuleBundleFactory
import com.prezi.spaghetti.gradle.PackageApplication
import com.prezi.spaghetti.gradle.SpaghettiBasePlugin
import com.prezi.spaghetti.gradle.SpaghettiExtension
import com.prezi.spaghetti.gradle.SpaghettiGeneratedSourceSet
import com.prezi.spaghetti.gradle.SpaghettiPlugin
import com.prezi.spaghetti.packaging.ApplicationType
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.gradle.language.base.BinaryContainer
import org.gradle.language.base.ProjectSourceSet
import org.gradle.language.base.internal.BinaryInternal
import org.slf4j.LoggerFactory

import javax.inject.Inject

/**
 * Add Spaghetti support to Haxe.
 */
class SpaghettiHaxePlugin implements Plugin<Project> {

	protected static final logger = LoggerFactory.getLogger(SpaghettiHaxePlugin)

	private final Instantiator instantiator

	@Inject
	SpaghettiHaxePlugin(Instantiator instantiator) {
		this.instantiator = instantiator
	}

	@Override
	void apply(Project project) {
		// Spaghetti will be working with Haxe, might as well set it
		project.plugins.apply(SpaghettiBasePlugin)
		def spaghettiExtension = project.extensions.getByType(SpaghettiExtension)
		spaghettiExtension.platform = "haxe"

		project.plugins.apply(HaxeBasePlugin)
		project.plugins.apply(SpaghettiPlugin)

		def binaryContainer = project.extensions.getByType(BinaryContainer)
		def projectSourceSet = project.extensions.getByType(ProjectSourceSet)
		def haxeExtension = project.extensions.getByType(HaxeExtension)

		// We'll be needing a "js" platform
		def targetPlatforms = haxeExtension.targetPlatforms
		targetPlatforms.maybeCreate("js")

		// Tests should always depend on modules
		def testConfiguration = project.configurations.getByName("test")
		testConfiguration.extendsFrom spaghettiExtension.configuration

		// Add Spaghetti generated sources to compile and source tasks
		projectSourceSet.findByName("main").withType(SpaghettiGeneratedSourceSet).all(new Action<SpaghettiGeneratedSourceSet>() {
			@Override
			void execute(SpaghettiGeneratedSourceSet spaghettiGeneratedSourceSet) {
				logger.debug("Adding ${spaghettiGeneratedSourceSet} to binaries in ${project.path}")
				binaryContainer.withType(HaxeBinaryBase).all(new Action<HaxeBinaryBase>() {
					@Override
					void execute(HaxeBinaryBase compiledBinary) {
						compiledBinary.source.add spaghettiGeneratedSourceSet
						logger.debug("Added ${spaghettiGeneratedSourceSet} to ${compiledBinary} in ${project.path}")
					}
				})
			}
		})

		// For every Haxe binary...
		binaryContainer.withType(HaxeBinary).all(new Action<HaxeBinary>() {
			@Override
			void execute(HaxeBinary binary) {
				// Add a compile, source and munit task
				HaxeBasePlugin.createCompileTask(project, binary, HaxeCompile)
				HaxeBasePlugin.createSourceTask(project, binary, Har)

				// Create Spaghetti compatible binary
				def jsBinary = instantiator.newInstance(DefaultHaxeCompiledSpaghettiCompatibleJavaScriptBinary, binary, false)
				jsBinary.builtBy(binary.getBuildDependencies())
				binaryContainer.add(jsBinary)
			}
		})

		binaryContainer.withType(HaxeTestBinary).all(new Action<HaxeTestBinary>() {
			@Override
			void execute(HaxeTestBinary testBinary) {
				HaxeBasePlugin.createTestCompileTask(project, testBinary, HaxeTestCompileWithSpaghetti)

				// Create Spaghetti compatible test binary
				def jsTestBinary = instantiator.newInstance(DefaultHaxeCompiledSpaghettiCompatibleJavaScriptBinary, testBinary, true)
				jsTestBinary.builtBy { testBinary.compileTask }
				binaryContainer.add(jsTestBinary)

				def appTask = createTestApplication(jsTestBinary)

				def munitTask = HaxeBasePlugin.createMUnitTask(project, testBinary, MUnitWithSpaghetti)
				munitTask.conventionMapping.testApplication = { appTask.getOutputDirectory() }
				munitTask.conventionMapping.testApplicationName = { jsTestBinary.name + '_test.js' }
				munitTask.dependsOn appTask
			}

			private PackageApplication createTestApplication(HaxeCompiledSpaghettiCompatibleJavaScriptBinary testBinary) {
				def namingScheme = ((BinaryInternal) testBinary).namingScheme
				def bundleTaskName = namingScheme.getTaskName("package")

				def appBundleTask = project.tasks.create(bundleTaskName, PackageApplication)
				appBundleTask.description = "Creates a testable applicaiton of ${testBinary}"
				appBundleTask.group = "test"
				appBundleTask.conventionMapping.outputDirectory = { project.file("${project.buildDir}/spaghetti/tests/" + testBinary.name) }
				appBundleTask.conventionMapping.additionalDirectDependentModulesInternal = { project.files(testBinary.bundleTask.getOutputDirectory()) }
				appBundleTask.conventionMapping.mainModule = { ModuleBundleFactory.load(testBinary.bundleTask.getOutputDirectory()).name }
				appBundleTask.conventionMapping.applicationName = { testBinary.name + '_test.js' }
				appBundleTask.conventionMapping.type = { ApplicationType.AMD }
				appBundleTask.conventionMapping.baseUrl = { '.' }
				appBundleTask.conventionMapping.execute = { false }
				appBundleTask.dependsOn testBinary.bundleTask
				return appBundleTask
			}
		})
	}
}
