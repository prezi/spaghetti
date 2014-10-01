package com.prezi.spaghetti.gradle;

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
	public static final String OBFUSCATED_CONFIGURATION_NAME = "modulesObf";

	private final Instantiator instantiator;

	@Inject
	public SpaghettiBasePlugin(Instantiator instantiator) {
		this.instantiator = instantiator;
	}

	@Override
	public void apply(final Project project) {
		Configuration defaultConfiguration = project.getConfigurations().maybeCreate(CONFIGURATION_NAME);
		Configuration defaultObfuscatedConfiguration = project.getConfigurations().maybeCreate(OBFUSCATED_CONFIGURATION_NAME);

		final SpaghettiExtension extension = project.getExtensions().create("spaghetti", SpaghettiExtension.class, project, instantiator, defaultConfiguration, defaultObfuscatedConfiguration);
		project.getTasks().withType(AbstractSpaghettiTask.class).all(new Action<AbstractSpaghettiTask>() {
			@Override
			public void execute(AbstractSpaghettiTask task) {
				task.getConventionMapping().map("dependentModules", new Callable<ConfigurableFileCollection>() {
					@Override
					public ConfigurableFileCollection call() throws Exception {
						return project.files(extension.getConfiguration());
					}
				});
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
}
