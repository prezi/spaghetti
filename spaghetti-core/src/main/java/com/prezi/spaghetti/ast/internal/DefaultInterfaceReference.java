package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.InterfaceReference;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeReference;

import java.util.ArrayList;
import java.util.List;

public class DefaultInterfaceReference extends AbstractTypeNodeReference<InterfaceNode> implements InterfaceReference {

	private final List<TypeReference> arguments = new ArrayList<TypeReference>();

	public DefaultInterfaceReference(InterfaceNode type, int arrayDimensions) {
		super(type, arrayDimensions);
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), arguments);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitInterfaceReference(this);
	}

	@Override
	public List<TypeReference> getArguments() {
		return arguments;
	}
}
