package com.prezi.spaghetti.gradle.internal;

import org.gradle.api.tasks.Input;

public class AbstractLanguageAwareSpaghettiTask extends AbstractSpaghettiTask {
	private String language;

	@Input
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void language(String language) {
		setLanguage(language);
	}
}
