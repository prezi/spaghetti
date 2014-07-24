package com.prezi.spaghetti.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

import java.util.concurrent.Callable;

public class SpaghettiBasePlugin implements Plugin<Project> {
	public static final String CONFIGURATION_NAME = "modules";
	public static final String OBFUSCATED_CONFIGURATION_NAME = "modulesObf";

	@Override
	public void apply(Project project) {
		Configuration defaultConfiguration = project.getConfigurations().maybeCreate(CONFIGURATION_NAME);
		Configuration defaultObfuscatedConfiguration = project.getConfigurations().maybeCreate(OBFUSCATED_CONFIGURATION_NAME);

		final SpaghettiExtension extension = project.getExtensions().create("spaghetti", SpaghettiExtension.class, defaultConfiguration, defaultObfuscatedConfiguration);
		project.getTasks().withType(AbstractSpaghettiTask.class).all(new Action<AbstractSpaghettiTask>() {
			@Override
			public void execute(AbstractSpaghettiTask task) {
				task.getConventionMapping().map("dependentModules", new Callable<Configuration>() {
					@Override
					public Configuration call() throws Exception {
						return extension.getConfiguration();
					}
				});
			}
		});
		project.getTasks().withType(AbstractPlatformAwareSpaghettiTask.class).all(new Action<AbstractPlatformAwareSpaghettiTask>() {
			@Override
			public void execute(AbstractPlatformAwareSpaghettiTask task) {
				task.getConventionMapping().map("platform", new Callable<String>() {
					@Override
					public String call() throws Exception {
						return extension.getPlatform();
					}
				});
			}
		});
	}
}
