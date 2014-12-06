package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.ImportNode;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;

public class DefaultImportNode extends AbstractNode implements ImportNode {

	private final FQName qualifiedName;
	private final String alias;

	public DefaultImportNode(Location location, FQName qualifiedName, String alias) {
		super(location);
		this.qualifiedName = qualifiedName;
		this.alias = alias;
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitImportNode(this);
	}

	@Override
	public String toString() {
		return qualifiedName.fullyQualifiedName;
	}

	@Override
	public final FQName getQualifiedName() {
		return qualifiedName;
	}

	@Override
	public final String getAlias() {
		return alias;
	}
}
