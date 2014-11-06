package com.prezi.spaghetti.kotlin.gradle;

import com.prezi.spaghetti.gradle.SpaghettiBasePlugin;
import com.prezi.spaghetti.gradle.SpaghettiPlugin;
import com.prezi.spaghetti.gradle.internal.SpaghettiExtension;
import com.prezi.spaghetti.gradle.internal.SpaghettiGeneratedSourceSet;
import com.prezi.spaghetti.gradle.internal.incubating.FunctionalSourceSet;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.internal.HasConvention;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.Callable;

public class SpaghettiKotlinPlugin implements Plugin<Project> {
	private static final Logger logger = LoggerFactory.getLogger(SpaghettiKotlinPlugin.class);

	@Override
	public void apply(final Project project) {
		project.getPlugins().apply(SpaghettiBasePlugin.class);
		final SpaghettiExtension spaghettiExtension = project.getExtensions().getByType(SpaghettiExtension.class);
		spaghettiExtension.setLanguage("kotlin");

		project.getPlugins().apply(KotlinPluginWrapper.class);
		project.getPlugins().apply(SpaghettiPlugin.class);

		final FunctionalSourceSet main = spaghettiExtension.getSources().getByName("main");
		final FunctionalSourceSet test = spaghettiExtension.getSources().getByName("test");

		// Add Spaghetti generated sources to compile and source tasks
		main.withType(SpaghettiGeneratedSourceSet.class).all(new Action<SpaghettiGeneratedSourceSet>() {
			@Override
			public void execute(final SpaghettiGeneratedSourceSet spaghettiGeneratedSourceSet) {
				addSpaghettiSourceSet(project, main, spaghettiGeneratedSourceSet, false);
			}
		});

		// Add Spaghetti generated test sources to test compile and test source tasks
		test.withType(SpaghettiGeneratedSourceSet.class).all(new Action<SpaghettiGeneratedSourceSet>() {
			@Override
			public void execute(final SpaghettiGeneratedSourceSet spaghettiGeneratedSourceSet) {
				addSpaghettiSourceSet(project, test, spaghettiGeneratedSourceSet, true);
			}
		});
	}

	private void addSpaghettiSourceSet(final Project project, FunctionalSourceSet functionalSourceSet, final SpaghettiGeneratedSourceSet spaghettiGeneratedSourceSet, final boolean usedForTesting) {
		logger.debug("Adding {} to Kotlin sources in {}", spaghettiGeneratedSourceSet, project.getPath());
		SourceSet sourceSet = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().maybeCreate(functionalSourceSet.getName());
		if (sourceSet instanceof HasConvention) {
//			KotlinSourceSet kotlinSourceSet = (KotlinSourceSet) ((HasConvention) sourceSet).getConvention().getPlugins().get("kotlin");
//			kotlinSourceSet.getKotlin().source(spaghettiGeneratedSourceSet.getSource());

			final AbstractCompile compileTask = (AbstractCompile) project.getTasks().getByName(sourceSet.getCompileJavaTaskName());
			compileTask.source(spaghettiGeneratedSourceSet);
			compileTask.dependsOn(spaghettiGeneratedSourceSet);

//			Callable<File> javaScriptFile = new CompiledFileFinder(compileTask, ".js", "JavaScript", false);
//			Callable<File> sourceMapFile = new CompiledFileFinder(compileTask, ".map", "source map", true);
//			SpaghettiPlugin.registerSpaghettiModuleBinary(project, sourceSet.getName(), javaScriptFile, sourceMapFile, Arrays.asList(compileTask), compileTask, new SpaghettiModuleFactory<AbstractCompile>() {
//				@Override
//				public SpaghettiModule create(BinaryNamingScheme namingScheme, SpaghettiModuleData data, AbstractCompile compile) {
//					KotlinSpaghettiModule moduleBinary = new KotlinSpaghettiModule(namingScheme, data, usedForTesting);
//					moduleBinary.builtBy(compile);
//					return moduleBinary;
//				}
//			});
		}
	}

	private static class CompiledFileFinder implements Callable<File> {
		private final AbstractCompile compileTask;
		private final String extension;
		private final String type;
		private final boolean allowMissing;

		public CompiledFileFinder(AbstractCompile compileTask, String extension, String type, boolean allowMissing) {
			this.compileTask = compileTask;
			this.extension = extension;
			this.type = type;
			this.allowMissing = allowMissing;
		}

		@Override
		public File call() throws Exception {
			File foundFile = null;
			File[] files = compileTask.getDestinationDir().listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.getName().endsWith(extension)) {
						if (foundFile != null) {
							throw new RuntimeException("Multiple " + type + " outputs found for task " + compileTask);
						}
						foundFile = file;
					}
				}
			}
			if (!allowMissing && foundFile == null) {
				throw new RuntimeException("No " + type + " output found for task " + compileTask);
			}
			return foundFile;
		}
	}
}
