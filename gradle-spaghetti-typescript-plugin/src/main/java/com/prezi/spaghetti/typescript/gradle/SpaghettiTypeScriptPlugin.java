package com.prezi.spaghetti.typescript.gradle;

import com.prezi.spaghetti.gradle.SpaghettiBasePlugin;
import com.prezi.spaghetti.gradle.SpaghettiExtension;
import com.prezi.spaghetti.gradle.SpaghettiGeneratedSourceSet;
import com.prezi.spaghetti.gradle.SpaghettiModule;
import com.prezi.spaghetti.gradle.SpaghettiModuleFactory;
import com.prezi.spaghetti.gradle.SpaghettiModuleData;
import com.prezi.spaghetti.gradle.SpaghettiPlugin;
import com.prezi.typescript.gradle.TypeScriptBinary;
import com.prezi.typescript.gradle.TypeScriptBinaryBase;
import com.prezi.typescript.gradle.TypeScriptPlugin;
import com.prezi.typescript.gradle.TypeScriptTestBinary;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.language.base.ProjectSourceSet;
import org.gradle.runtime.base.BinaryContainer;
import org.gradle.runtime.base.internal.BinaryNamingScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * Add Spaghetti support to TypeScript.
 */
public class SpaghettiTypeScriptPlugin implements Plugin<Project> {
	private static final Logger logger = LoggerFactory.getLogger(SpaghettiTypeScriptPlugin.class);

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

		binaryContainer.withType(TypeScriptBinary.class).all(new Action<TypeScriptBinary>() {
			@Override
			public void execute(final TypeScriptBinary binary) {
				registerSpaghettiModule(project, binary, false);
			}
		});
		binaryContainer.withType(TypeScriptTestBinary.class).all(new Action<TypeScriptTestBinary>() {
			@Override
			public void execute(TypeScriptTestBinary testBinary) {
				registerSpaghettiModule(project, testBinary, true);
			}
		});
	}

	private void registerSpaghettiModule(Project project, final TypeScriptBinaryBase binary, final boolean testing) {
		Callable<File> javaScriptFile = new Callable<File>() {
			@Override
			public File call() throws Exception {
				return binary.getCompileTask().getOutputFile();
			}
		};
		SpaghettiPlugin.registerSpaghettiModuleBinary(project, binary.getName(), javaScriptFile, null, Arrays.asList(binary), binary, new SpaghettiModuleFactory<TypeScriptBinaryBase>() {
			@Override
			public SpaghettiModule create(BinaryNamingScheme namingScheme, SpaghettiModuleData data, TypeScriptBinaryBase original) {
				TypeScriptSpaghettiModule moduleBinary = new TypeScriptSpaghettiModule(namingScheme, data, original, testing);
				moduleBinary.builtBy(original);
				return moduleBinary;
			}
		});
	}
}
