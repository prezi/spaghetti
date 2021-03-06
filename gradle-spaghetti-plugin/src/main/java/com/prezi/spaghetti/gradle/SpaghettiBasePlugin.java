package com.prezi.spaghetti.gradle;

import com.prezi.spaghetti.gradle.internal.AbstractDefinitionAwareSpaghettiTask;
import com.prezi.spaghetti.gradle.internal.AbstractLanguageAwareSpaghettiTask;
import com.prezi.spaghetti.gradle.internal.AbstractSpaghettiTask;
import com.prezi.spaghetti.gradle.internal.SpaghettiExtension;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.internal.reflect.Instantiator;

import javax.inject.Inject;
import java.util.concurrent.Callable;

public class SpaghettiBasePlugin implements Plugin<Project> {
	public static final String CONFIGURATION_NAME = "modules";
	public static final String DEF_CONFIGURATION_NAME = "modulesDef";
	public static final String LAZY_CONFIGURATION_NAME = "lazyModules";
	public static final String LAZY_DEF_CONFIGURATION_NAME = "lazyModulesDef";
	public static final String TEST_CONFIGURATION_NAME = "testModules";
	public static final String OBFUSCATED_CONFIGURATION_NAME = "modulesObf";
	public static final String TEST_OBFUSCATED_CONFIGURATION_NAME = "testModulesObf";

	private final Instantiator instantiator;

	@Inject
	public SpaghettiBasePlugin(Instantiator instantiator) {
		this.instantiator = instantiator;
	}

	@Override
	public void apply(final Project project) {
		Configuration defaultConfiguration = project.getConfigurations().maybeCreate(CONFIGURATION_NAME);
		Configuration defaultModuleDefinitionConfiguration = project.getConfigurations().maybeCreate(DEF_CONFIGURATION_NAME);
		Configuration defaultLazyConfiguration = project.getConfigurations().maybeCreate(LAZY_CONFIGURATION_NAME);
		Configuration defaultLazyModuleDefinitionConfiguration = project.getConfigurations().maybeCreate(LAZY_DEF_CONFIGURATION_NAME);
		Configuration defaultTestConfiguration = project.getConfigurations().maybeCreate(TEST_CONFIGURATION_NAME);
		defaultTestConfiguration.extendsFrom(defaultConfiguration);
		Configuration defaultObfuscatedConfiguration = project.getConfigurations().maybeCreate(OBFUSCATED_CONFIGURATION_NAME);
		Configuration defaultTestObfuscatedConfiguration = project.getConfigurations().maybeCreate(TEST_OBFUSCATED_CONFIGURATION_NAME);
		defaultTestObfuscatedConfiguration.extendsFrom(defaultObfuscatedConfiguration);

		final SpaghettiExtension extension = project.getExtensions().create("spaghetti", SpaghettiExtension.class, project, instantiator,
				defaultConfiguration,
				defaultModuleDefinitionConfiguration,
				defaultLazyConfiguration,
				defaultLazyModuleDefinitionConfiguration,
				defaultTestConfiguration,
				defaultObfuscatedConfiguration,
				defaultTestObfuscatedConfiguration);
		project.getTasks().withType(AbstractSpaghettiTask.class).all(new Action<AbstractSpaghettiTask>() {
			@Override
			public void execute(AbstractSpaghettiTask task) {
				withDefaultConfiguration(project, task);
			}
		});
		project.getTasks().withType(AbstractLanguageAwareSpaghettiTask.class).all(new Action<AbstractLanguageAwareSpaghettiTask>() {
			@Override
			public void execute(AbstractLanguageAwareSpaghettiTask task) {
				task.getConventionMapping().map("language", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return extension.getLanguage();
					}
				});
			}
		});
	}

	/**
	 * Override fallback configuration with the main configuration.
 	 */
	public static void withDefaultConfiguration(final Project project, AbstractSpaghettiTask task) {
		task.getConventionMapping().map("dependentModules", new Callable<ConfigurableFileCollection>() {
			@Override
			public ConfigurableFileCollection call() throws Exception {
				SpaghettiExtension extension = project.getExtensions().getByType(SpaghettiExtension.class);
				Configuration configuration = task.usesDirectDependentModuleImplementation()
					? extension.getConfiguration()
					: extension.getModuleDefinitionConfiguration();
				return project.files(configuration);
			}
		});
		task.getConventionMapping().map("lazyDependentModules", new Callable<ConfigurableFileCollection>() {
			@Override
			public ConfigurableFileCollection call() throws Exception {
				SpaghettiExtension extension = project.getExtensions().getByType(SpaghettiExtension.class);
				Configuration configuration = task.usesDirectDependentModuleImplementation()
						? extension.getLazyConfiguration()
						: extension.getLazyModuleDefinitionConfiguration();
				return project.files(configuration);
			}
		});
	}

	/**
	 * Override fallback configuration with the test configuration.
 	 */
	public static void withDefaultTestConfiguration(final Project project, AbstractSpaghettiTask task) {
		task.getConventionMapping().map("dependentModules", new Callable<ConfigurableFileCollection>() {
			@Override
			public ConfigurableFileCollection call() throws Exception {
				return project.files(project.getExtensions().getByType(SpaghettiExtension.class).getTestConfiguration());
			}
		});
	}
}
