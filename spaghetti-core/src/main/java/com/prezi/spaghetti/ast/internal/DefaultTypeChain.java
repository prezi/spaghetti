package com.prezi.spaghetti.ast.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeChain;
import com.prezi.spaghetti.ast.TypeReference;
import com.prezi.spaghetti.ast.VoidTypeReference;

import java.util.Collections;
import java.util.List;

public class DefaultTypeChain extends AbstractTypeReference implements TypeChain {
	private final List<TypeReference> elements = Lists.newArrayList();

	public DefaultTypeChain(Location location, int arrayDimensions) {
		super(location, arrayDimensions);
	}

	@Override
	public List<TypeReference> getParameters() {
		if (elements.size() == 2 && elements.get(0).equals(VoidTypeReference.VOID)) {
			return Collections.emptyList();
		}

		return elements.subList(0, elements.size() - 1);
	}

	@Override
	public TypeReference getReturnType() {
		return elements.get(elements.size() - 1);
	}

	@Override
	public TypeReference withAdditionalArrayDimensions(int extraDimensions) {
		DefaultTypeChain chain = new DefaultTypeChain(getLocation(), getArrayDimensions() + extraDimensions);
		chain.getElements().addAll(elements);
		return chain;
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), elements);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitTypeChain(this);
	}

	@Override
	public String toString() {
		return Joiner.on("->").join(elements);
	}

	@Override
	public List<TypeReference> getElements() {
		return elements;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DefaultTypeChain)) return false;
		if (!super.equals(o)) return false;

		DefaultTypeChain that = (DefaultTypeChain) o;

		if (!elements.equals(that.elements)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + elements.hashCode();
		return result;
	}
}
