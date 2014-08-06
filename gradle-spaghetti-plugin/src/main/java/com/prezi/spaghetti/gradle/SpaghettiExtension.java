package com.prezi.spaghetti.gradle;

import com.prezi.spaghetti.gradle.incubating.BinaryContainer;
import com.prezi.spaghetti.gradle.incubating.BinaryInternal;
import com.prezi.spaghetti.gradle.incubating.DefaultBinaryContainer;
import com.prezi.spaghetti.gradle.incubating.DefaultProjectSourceSet;
import com.prezi.spaghetti.gradle.incubating.ProjectSourceSet;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.internal.reflect.Instantiator;

public class SpaghettiExtension {
	private final ProjectSourceSet sources;
	private final BinaryContainer binaries;

	private String platform;
	private Configuration configuration;
	private Configuration obfuscatedConfiguration;
	private String sourceBaseUrl;

	public SpaghettiExtension(final Project project, Instantiator instantiator, Configuration defaultConfiguration, Configuration defaultObfuscatedConfiguration) {
		this.sources = instantiator.newInstance(DefaultProjectSourceSet.class, instantiator);
		this.binaries = instantiator.newInstance(DefaultBinaryContainer.class, instantiator);
		this.configuration = defaultConfiguration;
		this.obfuscatedConfiguration = defaultObfuscatedConfiguration;

		binaries.withType(BinaryInternal.class).all(new Action<BinaryInternal>() {
			public void execute(BinaryInternal binary) {
				Task binaryLifecycleTask = project.task(binary.getNamingScheme().getLifecycleTaskName());
				binaryLifecycleTask.setGroup("build");
				binaryLifecycleTask.setDescription(String.format("Assembles %s.", binary));
				binary.setBuildTask(binaryLifecycleTask);
			}
		});
	}

	public ProjectSourceSet getSources() {
		return sources;
	}

	public void sources(Action<ProjectSourceSet> action) {
		action.execute(sources);
	}

	public BinaryContainer getBinaries() {
		return binaries;
	}

	public void binaries(Action<BinaryContainer> action) {
		action.execute(binaries);
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public void platform(String platform) {
		setPlatform(platform);
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void configuration(Configuration configuration) {
		setConfiguration(configuration);
	}

	public Configuration getObfuscatedConfiguration() {
		return obfuscatedConfiguration;
	}

	public void setObfuscatedConfiguration(Configuration obfuscatedConfiguration) {
		this.obfuscatedConfiguration = obfuscatedConfiguration;
	}

	public void obfuscatedConfiguration(Configuration obfuscatedConfiguration) {
		setObfuscatedConfiguration(obfuscatedConfiguration);
	}

	public String getSourceBaseUrl() {
		return sourceBaseUrl;
	}

	public void setSourceBaseUrl(String sourceBaseUrl) {
		this.sourceBaseUrl = sourceBaseUrl;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void sourceBaseUrl(String source) {
		setSourceBaseUrl(source);
	}
}
