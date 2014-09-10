package com.prezi.spaghetti.haxe.gradle;

import com.google.common.collect.Maps;
import com.prezi.haxe.gradle.HaxeCommandBuilder;
import com.prezi.haxe.gradle.HaxeTestCompile;
import com.prezi.haxe.gradle.incubating.LanguageSourceSet;
import com.prezi.spaghetti.ReservedWords;
import com.prezi.spaghetti.haxe.HaxeGenerator;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import org.gradle.api.DomainObjectSet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class HaxeTestCompileWithSpaghetti extends HaxeTestCompile {
	@Override
	protected HaxeCommandBuilder configureHaxeCommandBuilder(File output, DomainObjectSet<LanguageSourceSet> sources) {
		HaxeCommandBuilder builder = super.configureHaxeCommandBuilder(output, sources);

		try {
			SimpleTemplateEngine engine = new SimpleTemplateEngine();
			Template template;
			try {
				template = engine.createTemplate(HaxeTestCompileWithSpaghetti.class.getResource("/SpaghettiTest.hx"));
			} catch (ClassNotFoundException ex) {
				throw new AssertionError(ex);
			}

			FileWriter writer = new FileWriter(new File(getTestsDirectory(), "SpaghettiTest.hx"));
			try {
				Map<String, Object> params = Maps.newHashMap();
				params.put("config", ReservedWords.SPAGHETTI_CLASS);
				params.put("haxeModule", HaxeGenerator.HAXE_MODULE_VAR);
				params.put("module", ReservedWords.MODULE);
				params.put("modules", ReservedWords.MODULES);
				template.make(params).writeTo(writer);
			} finally {
				writer.close();
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return builder;
	}
}
