package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.NamedNode

abstract class AbstractNamedNode extends AbstractNode implements NamedNode {
	final String name

	AbstractNamedNode(String name) {
		this.name = name
	}

	@Override
	int compareTo(NamedNode o) {
		return name.compareTo(o.name)
	}

	@Override
	String toString() {
		return name
	}
}
