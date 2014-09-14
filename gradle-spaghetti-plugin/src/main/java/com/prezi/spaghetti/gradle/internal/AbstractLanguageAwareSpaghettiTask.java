package com.prezi.spaghetti.gradle.internal;

import com.prezi.spaghetti.definition.ModuleConfiguration;
import com.prezi.spaghetti.generator.Generator;
import com.prezi.spaghetti.generator.Languages;
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

	protected Generator createGenerator(ModuleConfiguration config) {
		return Languages.createGeneratorForLanguage(getLanguage(), config);
	}
}
