package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.Platforms
import com.prezi.spaghetti.config.ModuleConfiguration
import org.gradle.api.tasks.Input

class AbstractPlatformAwareSpaghettiTask extends AbstractSpaghettiTask {
	@Input
	String platform

	protected Generator createGenerator(ModuleConfiguration config) {
		return Platforms.createGeneratorForPlatform(getPlatform(), config)
	}
}
