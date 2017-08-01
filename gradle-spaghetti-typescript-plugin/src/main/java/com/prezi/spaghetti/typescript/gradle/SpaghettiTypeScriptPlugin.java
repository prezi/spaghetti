package com.prezi.spaghetti.typescript.gradle;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.prezi.spaghetti.gradle.SpaghettiBasePlugin;
import com.prezi.spaghetti.gradle.SpaghettiPlugin;
import com.prezi.spaghetti.gradle.internal.SpaghettiExtension;
import com.prezi.spaghetti.gradle.internal.SpaghettiGeneratedSourceSet;
import com.prezi.spaghetti.gradle.internal.SpaghettiModule;
import com.prezi.spaghetti.gradle.internal.SpaghettiModuleData;
import com.prezi.spaghetti.gradle.internal.SpaghettiModuleFactory;
import com.prezi.spaghetti.gradle.internal.incubating.BinaryNamingScheme;
import com.prezi.spaghetti.typescript.gradle.internal.CopyDtsTask;
import com.prezi.spaghetti.typescript.gradle.internal.TypeScriptSpaghettiModule;
import com.prezi.typescript.gradle.TypeScriptBasePlugin;
import com.prezi.typescript.gradle.TypeScriptBinary;
import com.prezi.typescript.gradle.TypeScriptBinaryBase;
import com.prezi.typescript.gradle.TypeScriptExtension;
import com.prezi.typescript.gradle.TypeScriptPlugin;
import com.prezi.typescript.gradle.TypeScriptSourceSet;
import com.prezi.typescript.gradle.TypeScriptTestBinary;
import com.prezi.typescript.gradle.incubating.FunctionalSourceSet;
import com.prezi.typescript.gradle.incubating.LanguageSourceSet;


import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.internal.reflect.Instantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Add Spaghetti support to TypeScript.
 */
public class SpaghettiTypeScriptPlugin implements Plugin<Project> {
	private static final Logger logger = LoggerFactory.getLogger(SpaghettiTypeScriptPlugin.class);

	private final Instantiator instantiator;
	private final FileResolver fileResolver;

	@Inject
	public SpaghettiTypeScriptPlugin(Instantiator instantiator, FileResolver fileResolver) {
		this.instantiator = instantiator;
		this.fileResolver = fileResolver;
	}

	@Override
	public void apply(final Project project) {
		// Spaghetti will be working with TypeScript, might as well set it
		project.getPlugins().apply(SpaghettiBasePlugin.class);
		SpaghettiExtension spaghettiExtension = project.getExtensions().getByType(SpaghettiExtension.class);
		spaghettiExtension.setLanguage("typescript");

		project.getPlugins().apply(TypeScriptBasePlugin.class);
		project.getPlugins().apply(SpaghettiPlugin.class);

		final TypeScriptExtension typeScriptExtension = project.getExtensions().getByType(TypeScriptExtension.class);

		// Add Spaghetti generated sources to compile and source tasks
		spaghettiExtension.getSources().getByName("main").withType(SpaghettiGeneratedSourceSet.class).all(new Action<SpaghettiGeneratedSourceSet>() {
			@Override
			public void execute(final SpaghettiGeneratedSourceSet spaghettiGeneratedSourceSet) {
				addSpaghettiSourceSet(project, spaghettiGeneratedSourceSet, TypeScriptBinary.class, "spaghetti");
			}
		});

		// Add Spaghetti generated test sources to test compile and test source tasks
		spaghettiExtension.getSources().getByName("test").withType(SpaghettiGeneratedSourceSet.class).all(new Action<SpaghettiGeneratedSourceSet>() {
			@Override
			public void execute(final SpaghettiGeneratedSourceSet spaghettiGeneratedSourceSet) {
				addSpaghettiSourceSet(project, spaghettiGeneratedSourceSet, TypeScriptTestBinary.class, "spaghetti-test");
			}
		});

		typeScriptExtension.getBinaries().withType(TypeScriptBinary.class).all(new Action<TypeScriptBinary>() {
			@Override
			public void execute(final TypeScriptBinary binary) {
				registerSpaghettiModule(project, binary, false);
			}
		});
		typeScriptExtension.getBinaries().withType(TypeScriptTestBinary.class).all(new Action<TypeScriptTestBinary>() {
			@Override
			public void execute(TypeScriptTestBinary testBinary) {
				registerSpaghettiModule(project, testBinary, true);
			}
		});

		spaghettiExtension.registerDefinitionSearchSourceDirs(new Function<Void, Iterable<File>>() {
			public Iterable<File> apply(Void input) {
				Set<TypeScriptSourceSet> sources = typeScriptExtension.getSources().getByName("main").withType(TypeScriptSourceSet.class);

				Collection<Iterable<File>> sourceDirs = new HashSet<Iterable<File>>();
				for (TypeScriptSourceSet sourceSet : sources) {
					sourceDirs.add(sourceSet.getSource().getSrcDirs());
				}
				return Iterables.concat(sourceDirs);
			}
		});

		project.getPlugins().apply(TypeScriptPlugin.class);

		typeScriptExtension.getBinaries().withType(TypeScriptBinary.class).all(new Action<TypeScriptBinary>() {
			@Override
			public void execute(final TypeScriptBinary binary) {
				addCopyDtsTask(project, binary);
			}
		});
	}

	private <T extends TypeScriptBinaryBase> void addSpaghettiSourceSet(final Project project, final SpaghettiGeneratedSourceSet spaghettiGeneratedSourceSet, Class<T> binaryType, String sourceSetName) {
		logger.debug("Adding {} to binaries in {}", spaghettiGeneratedSourceSet, project.getPath());
		TypeScriptExtension typeScriptExtension = project.getExtensions().getByType(TypeScriptExtension.class);
		FunctionalSourceSet spaghetti = typeScriptExtension.getSources().maybeCreate(sourceSetName);
		final TypeScriptSourceSet typescriptSourceSet = instantiator.newInstance(TypeScriptSourceSet.class, spaghettiGeneratedSourceSet.getName(), spaghetti, fileResolver);
		typescriptSourceSet.getSource().source(spaghettiGeneratedSourceSet.getSource());
		typescriptSourceSet.builtBy(spaghettiGeneratedSourceSet);
		typeScriptExtension.getBinaries().withType(binaryType).all(new Action<T>() {
			@Override
			public void execute(T compiledBinary) {
				compiledBinary.getSource().add(typescriptSourceSet);
				logger.debug("Added {} to {} in {}", spaghettiGeneratedSourceSet, compiledBinary, project.getPath());
			}
		});
	}

	private void addCopyDtsTask(final Project project, TypeScriptBinary binary) {
		final com.prezi.typescript.gradle.incubating.BinaryNamingScheme namingScheme = binary.getNamingScheme();
		final CopyDtsTask copyDtsTask = project.getTasks().create(
			namingScheme.getTaskName("copyDtsFor"),
			CopyDtsTask.class);
		copyDtsTask.setDescription("Copies .d.ts for " + binary);
		copyDtsTask.dependsOn(binary.getCompileTask());
		copyDtsTask.setCompileTask(binary.getCompileTask());
		copyDtsTask.getConventionMapping().map("outputDir", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return project.file(project.getBuildDir() + "/compiled-typescript/"
						+ namingScheme.getOutputDirectoryBase() + "-dts/");
			}
		});
		binary.setCompileDtsTask(copyDtsTask);
		binary.builtBy(copyDtsTask);
		logger.debug("Added compile dts task {} for binary {} in {}", copyDtsTask, binary, project.getPath());
	}

	private void registerSpaghettiModule(Project project, final TypeScriptBinaryBase binary, final boolean testing) {
		Callable<File> javaScriptFile = new Callable<File>() {
			@Override
			public File call() throws Exception {
				return binary.getCompileTask().getConcatenatedOutputFile();
			}
		};
		Callable<File> definitionOverride = new Callable<File>() {
			@Override
			public File call() throws Exception {
				Task task = binary.getCompileDtsTask();
				if (task == null) {
					return null;
				}
				File file = task.getOutputs().getFiles().getSingleFile();
				if (!file.exists()) {
					return null;
				}
				return file;
			}
		};
		SpaghettiPlugin.registerSpaghettiModuleBinary(project, binary.getName(), javaScriptFile, null, definitionOverride, Arrays.asList(binary), binary, new SpaghettiModuleFactory<TypeScriptBinaryBase>() {
			@Override
			public SpaghettiModule create(BinaryNamingScheme namingScheme, SpaghettiModuleData data, TypeScriptBinaryBase original) {
				TypeScriptSpaghettiModule moduleBinary = new TypeScriptSpaghettiModule(namingScheme, data, original, testing);
				moduleBinary.builtBy(original);
				return moduleBinary;
			}
		});
	}
}
