package com.prezi.gradle.spaghetti.parse

import org.codehaus.groovy.control.CompilerConfiguration

/**
 * Created by lptr on 12/11/13.
 */
class ModuleParser {
	private String contents
	private String fileName

	ModuleParser(String contents, String fileName = "stdin") {
		this.contents = contents
		this.fileName = fileName
	}

	ModuleDefinition parse() {
		ModuleDefinition moduleDef = null

		def config = new CompilerConfiguration()
		config.scriptBaseClass = ModuleScript.name
		ModuleScript script = new GroovyShell(config).parse(contents) as ModuleScript
		script.run()

		moduleDef = script.context.module
		if (moduleDef == null) {
			throw new IllegalStateException("No module defined in ${fileName}")
		}
		return moduleDef
	}
}

abstract class ModuleScript extends Script {
	ParserContext context

	@Delegate
	private Parser parser

	public ModuleScript() {
		this.context = new ParserContext(binding)
		this.parser = new Parser(context)
	}

	void module(NamedArguments namedArgs) {
		def name = namedArgs.name
		Closure cl = ((Object[]) namedArgs.args)[0] as Closure
		println "Module ${name}"
		context.module = new ModuleDefinition(name, context)
		cl.delegate = context.module
		cl.resolveStrategy = Closure.DELEGATE_FIRST
		cl()
	}
}
