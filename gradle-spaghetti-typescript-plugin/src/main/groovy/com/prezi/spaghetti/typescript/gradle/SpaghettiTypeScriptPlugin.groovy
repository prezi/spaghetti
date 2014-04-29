package com.prezi.spaghetti.typescript.gradle

import com.prezi.spaghetti.gradle.SpaghettiBasePlugin
import com.prezi.spaghetti.gradle.SpaghettiExtension
import com.prezi.spaghetti.gradle.SpaghettiGeneratedSourceSet
import com.prezi.spaghetti.gradle.SpaghettiPlugin
import com.prezi.typescript.gradle.TypeScriptBinary
import com.prezi.typescript.gradle.TypeScriptPlugin
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.gradle.language.base.BinaryContainer
import org.gradle.language.base.ProjectSourceSet
import org.slf4j.LoggerFactory

import javax.inject.Inject

/**
 * Add Spaghetti support to TypeScript.
 */
class SpaghettiTypeScriptPlugin implements Plugin<Project> {

	private static final logger = LoggerFactory.getLogger(SpaghettiTypeScriptPlugin)

	private final Instantiator instantiator

	@Inject
	SpaghettiTypeScriptPlugin(Instantiator instantiator) {
		this.instantiator = instantiator
	}

	@Override
	@SuppressWarnings("UnnecessaryQualifiedReference")
	void apply(Project project) {
		// Spaghetti will be working with TypeScript, might as well set it
		project.plugins.apply(SpaghettiBasePlugin)
		def spaghettiExtension = project.extensions.getByType(SpaghettiExtension)
		spaghettiExtension.platform = "typescript"

		project.plugins.apply(TypeScriptPlugin)
		project.plugins.apply(SpaghettiPlugin)

		def binaryContainer = project.extensions.getByType(BinaryContainer)
		def projectSourceSet = project.extensions.getByType(ProjectSourceSet)

		// Add Spaghetti generated sources to compile and source tasks
		projectSourceSet.getByName("main").withType(SpaghettiGeneratedSourceSet).all(new Action<SpaghettiGeneratedSourceSet>() {
			@Override
			void execute(SpaghettiGeneratedSourceSet spaghettiGeneratedSourceSet) {
				logger.debug("Adding ${spaghettiGeneratedSourceSet} to binaries in ${project.path}")
				binaryContainer.withType(TypeScriptBinary).all(new Action<TypeScriptBinary>() {
					@Override
					void execute(TypeScriptBinary compiledBinary) {
						compiledBinary.source.add spaghettiGeneratedSourceSet
						SpaghettiTypeScriptPlugin.logger.debug("Added ${spaghettiGeneratedSourceSet} to ${compiledBinary} in ${project.path}")
					}
				})
			}
		})

		// For every TypeScript binary...
		binaryContainer.withType(TypeScriptBinary).all(new Action<TypeScriptBinary>() {
			@Override
			void execute(TypeScriptBinary binary) {
				// Create Spaghetti compatible binary
				def jsBinary = instantiator.newInstance(TypeScriptCompiledSpaghettiCompatibleJavaScriptBinary, binary)
				jsBinary.builtBy(binary.getBuildDependencies())
				binaryContainer.add(jsBinary)
				SpaghettiTypeScriptPlugin.logger.debug("Added ${jsBinary} in ${project.path}")
			}
		})
	}
}
