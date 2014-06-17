package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.AnnotationNode
import com.prezi.spaghetti.ast.ModuleVisitor

class DefaultAnnotationNode extends AbstractNamedNode implements AnnotationNode {
	final Map<String, Object> parameters

	DefaultAnnotationNode(String name, Map<String, Object> parameters) {
		super(name)
		this.parameters = parameters
	}

	@Override
	def <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitAnnotationNode(this)
	}

	@Override
	boolean hasDefaultParameter()
	{
		return parameters.containsKey(DEFAULT_PARAMETER)
	}

	@Override
	Object getDefaultParameter() {
		if (!hasDefaultParameter()) {
			throw new IllegalStateException("No default parameter specified for ${this}")
		}
		return parameters.get(DEFAULT_PARAMETER)
	}

	@Override
	boolean hasParameter(String parameter) {
		return parameters.containsKey(parameter)
	}

	@Override
	Object getParameter(String parameter) {
		if (!hasParameter(parameter)) {
			throw new IllegalStateException("No parameter '${parameter}' specified for ${this}")
		}
		return parameters.get(parameter);
	}

	@Override
	String toString() {
		return "@${name}(${parameters.collect { key, value -> "${key} = ${value}"}.join(", ")})"
	}
}

