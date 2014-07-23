package com.prezi.spaghetti.gradle;

import org.gradle.api.artifacts.Configuration;

public class SpaghettiExtension {
	private String platform;
	private Configuration configuration;
	private String sourceBaseUrl;

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

	public SpaghettiExtension(Configuration defaultConfiguration) {
		this.configuration = defaultConfiguration;
	}
}
