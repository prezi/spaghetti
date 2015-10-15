package com.prezi.spaghetti.ast.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeReference;

import java.util.Collections;
import java.util.List;

public class DefaultFunctionType extends AbstractTypeReference implements FunctionTypeInternal {
	private final List<TypeReferenceInternal> elementsInternal = Lists.newArrayList();
	private final List<TypeReference> elements = Collections.<TypeReference>unmodifiableList(elementsInternal);

	public DefaultFunctionType(Location location, int arrayDimensions) {
		super(location, arrayDimensions);
	}

	@Override
	public List<TypeReference> getParameters() {
		if (elements.size() == 2 && elements.get(0).equals(VoidTypeReferenceInternal.VOID)) {
			return Collections.emptyList();
		}

		return elements.subList(0, elements.size() - 1);
	}

	@Override
	public TypeReference getReturnType() {
		return elementsInternal.get(elementsInternal.size() - 1);
	}

	@Override
	public TypeReferenceInternal withAdditionalArrayDimensions(int extraDimensions) {
		DefaultFunctionType chain = new DefaultFunctionType(getLocation(), getArrayDimensions() + extraDimensions);
		chain.getElementsInternal().addAll(elementsInternal);
		return chain;
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), elementsInternal);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitFunctionType(this);
	}

	@Override
	public String toString() {
		return Joiner.on("->").join(elementsInternal);
	}

	@Override
	public List<TypeReference> getElements() {
		return elements;
	}

	@Override
	public List<TypeReferenceInternal> getElementsInternal() {
		return elementsInternal;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DefaultFunctionType)) return false;
		if (!super.equals(o)) return false;

		DefaultFunctionType that = (DefaultFunctionType) o;

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
