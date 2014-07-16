package com.prezi.spaghetti.typescript.gradle;

import com.prezi.spaghetti.gradle.SpaghettiBasePlugin;
import com.prezi.spaghetti.gradle.SpaghettiExtension;
import com.prezi.spaghetti.gradle.SpaghettiGeneratedSourceSet;
import com.prezi.spaghetti.gradle.SpaghettiPlugin;
import com.prezi.typescript.gradle.TypeScriptBinary;
import com.prezi.typescript.gradle.TypeScriptBinaryBase;
import com.prezi.typescript.gradle.TypeScriptPlugin;
import com.prezi.typescript.gradle.TypeScriptTestBinary;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.language.base.ProjectSourceSet;
import org.gradle.runtime.base.BinaryContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Add Spaghetti support to TypeScript.
 */
public class SpaghettiTypeScriptPlugin implements Plugin<Project> {
	@Inject
	public SpaghettiTypeScriptPlugin(Instantiator instantiator) {
		this.instantiator = instantiator;
	}

	@Override
	@SuppressWarnings("UnnecessaryQualifiedReference")
	public void apply(final Project project) {
		// Spaghetti will be working with TypeScript, might as well set it
		project.getPlugins().apply(SpaghettiBasePlugin.class);
		SpaghettiExtension spaghettiExtension = project.getExtensions().getByType(SpaghettiExtension.class);
		spaghettiExtension.setPlatform("typescript");

		project.getPlugins().apply(TypeScriptPlugin.class);
		project.getPlugins().apply(SpaghettiPlugin.class);

		final BinaryContainer binaryContainer = project.getExtensions().getByType(BinaryContainer.class);
		ProjectSourceSet projectSourceSet = project.getExtensions().getByType(ProjectSourceSet.class);

		// Add Spaghetti generated sources to compile and source tasks
		projectSourceSet.getByName("main").withType(SpaghettiGeneratedSourceSet.class).all(new Action<SpaghettiGeneratedSourceSet>() {
			@Override
			public void execute(final SpaghettiGeneratedSourceSet spaghettiGeneratedSourceSet) {
				logger.debug("Adding {} to binaries in {}", spaghettiGeneratedSourceSet, project.getPath());
				binaryContainer.withType(TypeScriptBinaryBase.class).all(new Action<TypeScriptBinaryBase>() {
					@Override
					public void execute(TypeScriptBinaryBase compiledBinary) {
						compiledBinary.getSource().add(spaghettiGeneratedSourceSet);
						logger.debug("Added {} to {} in {}", spaghettiGeneratedSourceSet, compiledBinary, project.getPath());
					}

				});
			}

		});

		// For every TypeScript binary...
		binaryContainer.withType(TypeScriptBinary.class).all(new Action<TypeScriptBinary>() {
			@Override
			public void execute(TypeScriptBinary binary) {
				// Create Spaghetti compatible binary
				TypeScriptCompiledSpaghettiCompatibleJavaScriptBinary jsBinary = instantiator.newInstance(TypeScriptCompiledSpaghettiCompatibleJavaScriptBinary.class, binary, false);
				jsBinary.builtBy(binary);
				binaryContainer.add(jsBinary);
				logger.debug("Added {} in {}", jsBinary, project.getPath());
			}

		});

		// For every TypeScript test binary...
		binaryContainer.withType(TypeScriptTestBinary.class).all(new Action<TypeScriptTestBinary>() {
			@Override
			public void execute(TypeScriptTestBinary testBinary) {
				// Create Spaghetti compatible test binary
				TypeScriptCompiledSpaghettiCompatibleJavaScriptBinary jsTestBinary = instantiator.newInstance(TypeScriptCompiledSpaghettiCompatibleJavaScriptBinary.class, testBinary, true);
				jsTestBinary.builtBy(testBinary);
				binaryContainer.add(jsTestBinary);
				logger.debug("Added {} in {}", jsTestBinary, project.getPath());
			}

		});
	}

	private static final Logger logger = LoggerFactory.getLogger(SpaghettiTypeScriptPlugin.class);
	private final Instantiator instantiator;
}
