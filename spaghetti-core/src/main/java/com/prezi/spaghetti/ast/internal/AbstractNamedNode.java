package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.NamedNode;

public abstract class AbstractNamedNode extends AbstractNode implements NamedNode {
	protected final String name;

	public AbstractNamedNode(Location location, String name) {
		super(location);
		this.name = name;
	}

	@Override
	public int compareTo(NamedNode o) {
		return name.compareTo(o.getName());
	}

	@Override
	public String toString() {
		return name;
	}

	public final String getName() {
		return name;
	}
}
