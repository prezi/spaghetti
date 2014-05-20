package com.prezi.spaghetti.haxe.gradle

import com.prezi.haxe.gradle.HaxeCommandBuilder
import com.prezi.haxe.gradle.HaxeTestCompile
import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.ReservedWords
import com.prezi.spaghetti.haxe.HaxeGenerator
import groovy.text.SimpleTemplateEngine
import org.gradle.api.DomainObjectSet
import org.gradle.language.base.LanguageSourceSet

/**
 * Created by lptr on 20/05/14.
 */
class HaxeTestCompileWithSpaghetti extends HaxeTestCompile {
	@Override
	protected HaxeCommandBuilder configureHaxeCommandBuilder(File output, DomainObjectSet<LanguageSourceSet> sources) {
		def builder = super.configureHaxeCommandBuilder(output, sources)

		def engine = new SimpleTemplateEngine()
		def template = engine.createTemplate(HaxeTestCompileWithSpaghetti.class.getResource("/SpaghettiTest.hx"))
		new File(getTestsDirectory(), "SpaghettiTest.hx") << template.make(
				config: Generator.CONFIG,
				haxeModule: HaxeGenerator.HAXE_MODULE_VAR,
				module: ReservedWords.MODULE,
				modules: ReservedWords.MODULES,
		)

		return builder
	}
}
