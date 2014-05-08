package com.prezi.spaghetti.haxe.gradle

import com.prezi.haxe.gradle.HaxeExtension
import com.prezi.haxe.gradle.Har
import com.prezi.haxe.gradle.HaxeBasePlugin
import com.prezi.haxe.gradle.HaxeBinary
import com.prezi.spaghetti.gradle.SpaghettiBasePlugin
import com.prezi.spaghetti.gradle.SpaghettiExtension
import com.prezi.spaghetti.gradle.SpaghettiGeneratedSourceSet
import com.prezi.spaghetti.gradle.SpaghettiPlugin
import com.prezi.spaghetti.gradle.SpaghettiResourceSet
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.gradle.language.base.BinaryContainer
import org.gradle.language.base.ProjectSourceSet
import org.slf4j.LoggerFactory

import javax.inject.Inject

/**
 * Add Spaghetti support to Haxe.
 */
class SpaghettiHaxePlugin implements Plugin<Project> {

	private static final logger = LoggerFactory.getLogger(SpaghettiHaxePlugin)

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
				binaryContainer.withType(HaxeBinary).all(new Action<HaxeBinary>() {
					@Override
					void execute(HaxeBinary compiledBinary) {
						compiledBinary.source.add spaghettiGeneratedSourceSet
						SpaghettiHaxePlugin.logger.debug("Added ${spaghettiGeneratedSourceSet} to ${compiledBinary} in ${project.path}")
					}
				})
			}
		})

		// For every Haxe binary...
		binaryContainer.withType(HaxeBinary).all(new Action<HaxeBinary>() {
			@Override
			void execute(HaxeBinary binary) {
				// Create Spaghetti compatible binary
				def jsBinary = instantiator.newInstance(DefaultHaxeCompiledSpaghettiCompatibleJavaScriptBinary, binary)
				jsBinary.builtBy(binary.getBuildDependencies())
				binaryContainer.add(jsBinary)

				// Add a compile, source and munit task
				HaxeBasePlugin.createCompileTask(project, binary, HaxeCompileWithSpaghetti)
				HaxeBasePlugin.createSourceTask(project, binary, Har)
				MUnitWithSpaghetti munit = (MUnitWithSpaghetti) HaxeBasePlugin.createMUnitTask(project, binary, MUnitWithSpaghetti)
				projectSourceSet.findByName("main").withType(SpaghettiResourceSet).all(new Action<SpaghettiResourceSet>() {
					@Override
					void execute(SpaghettiResourceSet spaghettiResourceSet) {
						munit.spaghettiResources(spaghettiResourceSet)
					}
				})
			}
		})
	}
}
