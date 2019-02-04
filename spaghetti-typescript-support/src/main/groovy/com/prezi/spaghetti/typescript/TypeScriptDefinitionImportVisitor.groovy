package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.EnumReference
import com.prezi.spaghetti.ast.ExternInterfaceReference
import com.prezi.spaghetti.ast.InterfaceReference
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.ModuleVisitorBase
import com.prezi.spaghetti.ast.StructReference
import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.TypeParameterReference
import com.prezi.spaghetti.generator.GeneratorUtils

class TypeScriptDefinitionImportVisitor extends ModuleVisitorBase<Set<String>> {
	private String currentNamespace

	public static String collectImports(ModuleNode node) {
		return namespacesToImports(new TypeScriptDefinitionImportVisitor(node.name).visit(node))
	}

	public static String namespacesToImports(Set<String> namespaces) {
		List<String> list = namespaces.toList()
		list.sort();
		return list.collect { ns ->
			"import * as ${ns} from \"${ns}\";\n"
		}.join("")
	}

	TypeScriptDefinitionImportVisitor(String currentNamespace) {
		assert currentNamespace != null
		this.currentNamespace = currentNamespace
	}

	@Override
	protected Set<String> defaultResult() {
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<String> aggregateResult(Set<String> aggregate, Set<String> nextResult) {
		return aggregate + nextResult;
	}

	@Override
	Set<String> visitInterfaceReference(InterfaceReference reference) {
		return processNamespace(reference.type.qualifiedName)
	}

	@Override
	Set<String> visitStructReference(StructReference reference) {
		return processNamespace(reference.type.qualifiedName)
	}

	@Override
	Set<String> visitEnumReference(EnumReference reference) {
		return processNamespace(reference.type.qualifiedName)
	}

	@Override
	Set<String> visitTypeParameterReference(TypeParameterReference reference) {
		return processNamespace(reference.type.qualifiedName)
	}

	@Override
	Set<String> visitExternInterfaceReference(ExternInterfaceReference reference) {
		return processNamespace(reference.type.qualifiedName)
	}

	private Set<String> processNamespace(FQName name) {
		if (!name.hasNamespace() || name.namespace == currentNamespace) {
			return Collections.EMPTY_SET;
		} else {
			return Collections.singleton(
				GeneratorUtils.namespaceToIdentifier(name.namespace))
		}
	}
}
