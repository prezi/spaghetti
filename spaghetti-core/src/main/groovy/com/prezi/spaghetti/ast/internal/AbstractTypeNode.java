package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.TypeNode;

public abstract class AbstractTypeNode extends AbstractNamedNode implements TypeNode {
	private final FQName qualifiedName;

	public AbstractTypeNode(FQName qualifiedName) {
		super(qualifiedName.localName);
		this.qualifiedName = qualifiedName;
	}

	@Override
	public String toString() {
		return qualifiedName.toString();
	}

	public FQName getQualifiedName() {
		return qualifiedName;
	}
}
