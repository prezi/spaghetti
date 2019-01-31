package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.generator.AbstractStubGenerator
import com.prezi.spaghetti.generator.GeneratorParameters
import com.prezi.spaghetti.generator.GeneratorUtils
import com.prezi.spaghetti.typescript.stub.TypeScriptInterfaceStubGeneratorVisitor

class TypeScriptStubGenerator extends AbstractStubGenerator {
	TypeScriptStubGenerator() {
		super("typescript")
	}

	@Override
	void generateStubs(GeneratorParameters params, File outputDirectory) throws IOException {
		def config = params.moduleConfiguration
		def header = params.header
		config.allModules.each { module ->
			def contents = ""
			for (type in module.types) {
				if (type instanceof InterfaceNode) {
					contents += new TypeScriptInterfaceStubGeneratorVisitor().visit(type)
				}
			}
			TypeScriptUtils.createSourceFile(header, GeneratorUtils.namespaceToIdentifier(module.name) + ".stubs", outputDirectory, contents)
		}
	}
}
