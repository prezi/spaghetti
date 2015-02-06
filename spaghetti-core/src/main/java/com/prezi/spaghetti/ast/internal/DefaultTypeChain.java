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
	private final List<TypeReference> elementsInternal = Lists.newArrayList();
	private final List<TypeReference> elements = Collections.unmodifiableList(elementsInternal);

	public DefaultTypeChain(Location location, int arrayDimensions) {
		super(location, arrayDimensions);
	}

	@Override
	public List<TypeReference> getParameters() {
		if (elementsInternal.size() == 2 && elementsInternal.get(0).equals(VoidTypeReference.VOID)) {
			return Collections.emptyList();
		}

		return elementsInternal.subList(0, elementsInternal.size() - 1);
	}

	@Override
	public TypeReference getReturnType() {
		return elementsInternal.get(elementsInternal.size() - 1);
	}

	@Override
	public TypeReference withAdditionalArrayDimensions(int extraDimensions) {
		DefaultTypeChain chain = new DefaultTypeChain(getLocation(), getArrayDimensions() + extraDimensions);
		chain.getElementsInternal().addAll(elementsInternal);
		return chain;
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), elementsInternal);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitTypeChain(this);
	}

	@Override
	public String toString() {
		return Joiner.on("->").join(elementsInternal);
	}

	@Override
	public List<TypeReference> getElements() {
		return elements;
	}

	public List<TypeReference> getElementsInternal() {
		return elementsInternal;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DefaultTypeChain)) return false;
		if (!super.equals(o)) return false;

		DefaultTypeChain that = (DefaultTypeChain) o;

		if (!elementsInternal.equals(that.elementsInternal)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + elementsInternal.hashCode();
		return result;
	}
}
