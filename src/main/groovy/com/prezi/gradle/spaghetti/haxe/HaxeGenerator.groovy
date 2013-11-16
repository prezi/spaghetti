package com.prezi.gradle.spaghetti.haxe

import com.prezi.gradle.spaghetti.Generator
import com.prezi.gradle.spaghetti.ModuleConfiguration
import org.gradle.api.Project
/**
 * Created by lptr on 12/11/13.
 */
class HaxeGenerator implements Generator {
	private Project project

	@Override
	void initialize(Project project)
	{
		this.project = project
	}

	@Override
	String getPlatform()
	{
		return "haxe"
	}

	@Override
	void generateInterfaces(ModuleConfiguration config, File outputDirectory)
	{
		config.modules.values().each { module ->
			new HaxeInterfaceGeneratorVisitor(config, module, outputDirectory).visit(module.context)
		}
	}

	@Override
	void generateClientModule(ModuleConfiguration config, File outputDirectory)
	{
		config.modules.values().each { module ->
			new HaxeTypedefGeneratorVisitor(config, module, outputDirectory).visit(module.context)
		}
	}
}
