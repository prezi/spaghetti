package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;

public class DefaultTypeParameterNode extends AbstractTypeNode implements TypeParameterNodeInternal {
	public DefaultTypeParameterNode(Location location, String name) {
		super(location, DefaultFQName.fromString(name));
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitTypeParameterNode(this);
	}

}
