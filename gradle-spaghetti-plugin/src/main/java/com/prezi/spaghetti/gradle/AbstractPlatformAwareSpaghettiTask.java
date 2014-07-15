package com.prezi.spaghetti.gradle;

import com.prezi.spaghetti.Generator;
import com.prezi.spaghetti.Platforms;
import com.prezi.spaghetti.config.ModuleConfiguration;
import org.gradle.api.tasks.Input;

public class AbstractPlatformAwareSpaghettiTask extends AbstractSpaghettiTask {
	private String platform;

	@Input
	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public void platform(String platform) {
		setPlatform(platform);
	}

	protected Generator createGenerator(ModuleConfiguration config) {
		return Platforms.createGeneratorForPlatform(getPlatform(), config);
	}
}
