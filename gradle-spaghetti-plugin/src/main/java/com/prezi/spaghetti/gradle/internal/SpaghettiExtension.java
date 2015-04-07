package com.prezi.spaghetti.gradle.internal;

import com.prezi.spaghetti.gradle.internal.incubating.BinaryContainer;
import com.prezi.spaghetti.gradle.internal.incubating.BinaryInternal;
import com.prezi.spaghetti.gradle.internal.incubating.DefaultBinaryContainer;
import com.prezi.spaghetti.gradle.internal.incubating.DefaultProjectSourceSet;
import com.prezi.spaghetti.gradle.internal.incubating.ProjectSourceSet;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.internal.reflect.Instantiator;

public class SpaghettiExtension {
	private final ProjectSourceSet sources;
	private final BinaryContainer binaries;

	private String language;
	private Configuration configuration;
	private Configuration testConfiguration;
	private Configuration obfuscatedConfiguration;
	private Configuration testObfuscatedConfiguration;
	private Configuration moduleDefinitionConfiguration;
	private String sourceBaseUrl;

	public SpaghettiExtension(final Project project, Instantiator instantiator, Configuration defaultConfiguration, Configuration defaultTestConfiguration, Configuration defaultObfuscatedConfiguration, Configuration defaultTestObfuscatedConfiguration, Configuration defaultModuleDefinitionConfiguration) {
		this.sources = instantiator.newInstance(DefaultProjectSourceSet.class, instantiator);
		this.binaries = instantiator.newInstance(DefaultBinaryContainer.class, instantiator);
		this.configuration = defaultConfiguration;
		this.obfuscatedConfiguration = defaultObfuscatedConfiguration;
		this.testConfiguration = defaultTestConfiguration;
		this.testObfuscatedConfiguration = defaultTestObfuscatedConfiguration;
		this.moduleDefinitionConfiguration = defaultModuleDefinitionConfiguration;

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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void language(String language) {
		setLanguage(language);
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

	public Configuration getTestConfiguration() {
		return testConfiguration;
	}

	public void setTestConfiguration(Configuration testConfiguration) {
		this.testConfiguration = testConfiguration;
	}

	public void testConfiguration(Configuration testConfiguration) {
		setTestConfiguration(testConfiguration);
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

	public Configuration getTestObfuscatedConfiguration() {
		return testObfuscatedConfiguration;
	}

	public void setTestObfuscatedConfiguration(Configuration testObfuscatedConfiguration) {
		this.testObfuscatedConfiguration = testObfuscatedConfiguration;
	}

	public void testObfuscatedConfiguration(Configuration testObfuscatedConfiguration) {
		setTestObfuscatedConfiguration(testObfuscatedConfiguration);
	}

	public Configuration getModuleDefinitionConfiguration() {
		return moduleDefinitionConfiguration;
	}

	public void setModuleDefinitionConfiguration(Configuration moduleDefinitionConfiguration) {
		this.moduleDefinitionConfiguration = moduleDefinitionConfiguration;
	}

	public void moduleDefinitionConfiguration(Configuration moduleDefinitionConfiguration) {
		setModuleDefinitionConfiguration(moduleDefinitionConfiguration);
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
