package com.prezi.gradle.spaghetti

/**
 * Created by lptr on 12/11/13.
 */
class ModuleParser {
	File file

	ModuleParser(File file) {
		this.file = file
	}

	ModuleDefinition parse() {
		ModuleDefinition moduleDef = null

		Script script = new GroovyShell().parse(file.text)

		def emc = new ExpandoMetaClass(script.class)
		emc.Int = new Type("Int")
		emc.Void = new Type("Void")
		emc.module = { String name, Closure cl ->
			println "Module ${name}"
			moduleDef = new ModuleDefinition(name)
			cl.delegate = moduleDef
			cl.resolveStrategy = Closure.DELEGATE_FIRST
			cl()
		}
		emc.initialize()
		script.metaClass = emc
		script.run()

		if (moduleDef == null) {
			throw new IllegalStateException("No module defined in ${file}")
		}
		return moduleDef
	}
}
