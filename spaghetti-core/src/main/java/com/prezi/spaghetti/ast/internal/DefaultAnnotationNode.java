package com.prezi.spaghetti.ast.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;

import java.util.Map;

public class DefaultAnnotationNode extends AbstractNamedNode implements AnnotationNode {
	private final Map<String, Object> parameters;

	public DefaultAnnotationNode(Location location, String name, Map<String, Object> parameters) {
		super(location, name);
		this.parameters = ImmutableMap.copyOf(parameters);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitAnnotationNode(this);
	}

	@Override
	public boolean hasDefaultParameter() {
		return parameters.containsKey(DEFAULT_PARAMETER);
	}

	@Override
	public Object getDefaultParameter() {
		if (!hasDefaultParameter()) {
			throw new IllegalStateException("No default parameter specified for " + this);
		}

		return parameters.get(DEFAULT_PARAMETER);
	}

	@Override
	public boolean hasParameter(String parameter) {
		return parameters.containsKey(parameter);
	}

	@Override
	public Object getParameter(final String parameter) {
		if (!hasParameter(parameter)) {
			throw new IllegalStateException("No parameter \'" + parameter + "\' specified for " + this);
		}

		return parameters.get(parameter);
	}

	@Override
	public String toString() {
		return "@" + name + "(" + Joiner.on(", ").withKeyValueSeparator(" = ").join(parameters) + ")";
	}

	@Override
	public final Map<String, Object> getParameters() {
		return parameters;
	}
}
