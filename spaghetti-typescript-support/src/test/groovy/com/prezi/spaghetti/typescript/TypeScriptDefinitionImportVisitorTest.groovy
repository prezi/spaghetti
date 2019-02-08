package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.generator.ModuleGeneratorSpecification

class TypeScriptDefinitionImportVisitorTest extends ModuleGeneratorSpecification {
	def "generate"() {
		def definition = """
module com.example.test {
	import com.example.other.Parent;
	import com.example.other1.Other;

	interface Self {
		a(): string;
	}

	interface MyInterface<X> extends Parent<X> {
		doSomething(value: Self): Other;
	}
}
"""
		def namespaces = parseAndVisitModule(
				definition,
				new TypeScriptDefinitionImportVisitor(getNamespace()),
				mockInterface("Parent", "com.example.other.Parent", mockTypeParameter()),
				mockInterface("Other", "com.example.other1.Other"))

		def result = TypeScriptDefinitionImportVisitor.namespacesToImports(namespaces)

		expect:
		result == """import * as com_example_other from "com.example.other";
import * as com_example_other1 from "com.example.other1";
"""
	}
}
