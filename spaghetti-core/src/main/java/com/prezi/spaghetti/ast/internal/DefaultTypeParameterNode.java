package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeParameterNode;

public class DefaultTypeParameterNode extends AbstractTypeNode implements TypeParameterNode {
	public DefaultTypeParameterNode(Location location, String name) {
		super(location, FQName.fromString(name));
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitTypeParameterNode(this);
	}

}
