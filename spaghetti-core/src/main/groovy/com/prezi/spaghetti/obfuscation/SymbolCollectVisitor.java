package com.prezi.spaghetti.obfuscation;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.ConstEntryNode;
import com.prezi.spaghetti.ast.ConstNode;
import com.prezi.spaghetti.ast.EnumNode;
import com.prezi.spaghetti.ast.EnumValueNode;
import com.prezi.spaghetti.ast.ExternNode;
import com.prezi.spaghetti.ast.InterfaceMethodNode;
import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.ModuleMethodNode;
import com.prezi.spaghetti.ast.ModuleVisitorBase;
import com.prezi.spaghetti.ast.PropertyNode;
import com.prezi.spaghetti.ast.StructNode;

import java.util.Collection;
import java.util.Collections;


class SymbolCollectVisitor extends ModuleVisitorBase<Collection<String>> {

	@Override
	public Collection<String> aggregateResult(Collection<String> aggregate, Collection<String> nextResult) {
		return Sets.newLinkedHashSet(Iterables.concat(aggregate, nextResult));
	}

	@Override
	protected Collection<String> defaultResult() {
		return Collections.emptySet();
	}

	@Override
	public Collection<String> visitExternNode(ExternNode node) {
		return node.getQualifiedName().getParts();
	}

	@Override
	public Collection<String> visitInterfaceNode(InterfaceNode node) {
		return Collections2.transform(node.getMethods(), new Function<InterfaceMethodNode, String>() {
			@Override
			public String apply(InterfaceMethodNode method) {
				return method.getName();
			}
		});
	}

	@Override
	public Collection<String> visitStructNode(StructNode node) {
		return Collections2.transform(node.getProperties(), new Function<PropertyNode, String>() {
			@Override
			public String apply(PropertyNode property) {
				return property.getName();
			}
		});
	}

	@Override
	public Collection<String> visitConstNode(ConstNode node) {
		return Collections2.transform(node.getEntries(), new Function<ConstEntryNode, String>() {
			@Override
			public String apply(ConstEntryNode constEntry) {
				return constEntry.getName();
			}
		});
	}

	@Override
	public Collection<String> visitEnumNode(EnumNode node) {
		return Collections2.transform(node.getValues(), new Function<EnumValueNode, String>() {
			@Override
			public String apply(EnumValueNode enumValue) {
				return enumValue.getName();
			}
		});
	}

	@Override
	public Collection<String> visitModuleMethodNode(ModuleMethodNode node) {
		return Collections.singleton(node.getName());
	}
}
