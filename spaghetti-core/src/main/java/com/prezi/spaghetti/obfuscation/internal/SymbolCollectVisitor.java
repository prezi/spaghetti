package com.prezi.spaghetti.obfuscation.internal;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.ConstNode;
import com.prezi.spaghetti.ast.EnumNode;
import com.prezi.spaghetti.ast.ExternInterfaceNode;
import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.ast.ModuleVisitorBase;
import com.prezi.spaghetti.ast.NamedNode;
import com.prezi.spaghetti.ast.StructNode;

import java.util.Collection;
import java.util.Collections;


public class SymbolCollectVisitor extends ModuleVisitorBase<Collection<String>> {

	@Override
	public Collection<String> aggregateResult(Collection<String> aggregate, Collection<String> nextResult) {
		return Sets.newLinkedHashSet(Iterables.concat(aggregate, nextResult));
	}

	@Override
	protected Collection<String> defaultResult() {
		return Collections.emptySet();
	}

	@Override
	public Collection<String> visitExternInterfaceNode(ExternInterfaceNode node) {
		return node.getQualifiedName().getParts();
	}

	@Override
	public Collection<String> visitModuleNode(ModuleNode node) {
		return Sets.newLinkedHashSet(Iterables.concat(ImmutableList.of(node.getAlias()), super.visitModuleNode(node), extractNames(node.getMethods())));
	}

	@Override
	public Collection<String> visitInterfaceNode(InterfaceNode node) {
		return extractNames(node.getMethods());
	}

	@Override
	public Collection<String> visitStructNode(StructNode node) {
		Collection<String> properties = extractNames(node.getProperties());
		Collection<String> methods = extractNames(node.getMethods());
		return ImmutableList.<String>builder().addAll(properties).addAll(methods).build();
	}

	@Override
	public Collection<String> visitConstNode(ConstNode node) {
		return ImmutableList.<String>builder().add(node.getName()).addAll(extractNames(node.getEntries())).build();
	}

	@Override
	public Collection<String> visitEnumNode(EnumNode node) {
		return ImmutableList.<String>builder().add(node.getName()).addAll(extractNames(node.getValues())).build();
	}

	private static Collection<String> extractNames(Collection<? extends NamedNode> nodes) {
		return Collections2.transform(nodes, new Function<NamedNode, String>() {
			@Override
			public String apply(NamedNode node) {
				return node.getName();
			}
		});
	}
}
